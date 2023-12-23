package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.util.GivenContext.{ContextNotGivenException, FolderClass, RequestItemClass}
import cc.sukazyo.messiva.utils.StackUtils

import scala.annotation.targetName
import scala.collection.mutable
import scala.reflect.{classTag, ClassTag}
import scala.util.boundary

object GivenContext {
	case class FolderClass (clazz: Option[Class[?]])
	object FolderClass:
		def default: FolderClass = FolderClass(None)
	case class RequestItemClass (clazz: Class[?])
	private def lastNonGCStack: StackTraceElement =
		boundary {
			for (stack <- StackUtils.getStackTrace(0)) {
				if (!stack.getClassName.startsWith(classOf[GivenContext].getName))
					boundary break stack
			}
			StackTraceElement("unknown", "unknown", "unknown", -1)
		}
	class ContextNotGivenException (using
		val requestItemClass: RequestItemClass,
		val folderClass: FolderClass = FolderClass.default,
		val requestStack: StackTraceElement = UseStacks.getStackHeadBeforeClass[GivenContext]
	) extends NoSuchElementException (
		s"None of the ${requestItemClass.clazz.getSimpleName} is in the context${folderClass.clazz.map(" and owned by " + _.getSimpleName).getOrElse("")}, which is required by $requestStack."
	)
}

/** A mutable collection that can store(provide) any typed value and read(use/consume) that value by type.
  *
  * ## Simple Guide
  * {{{
  *     val cxt = GivenContext()
  *     class BaseClass {}
  *     class MyImplementation extends BaseClass {}
  *
  *
  *     cxt.provide(true)                                    // this provides a Boolean
  *     cxt.provide[BaseClass](new MyImplementation())       // although this object is of type MyImplementation, but it is stored
  *                                                          // as BaseClass so you can (and actually can only) read it using BaseClass
  *     cxt << "string"
  *     cxt << classOf[Int] -> 1                             // you can also manually set the stored type using this method
  *
  *
  *     cxt >> { (i: Int) => println(i) } || { println("no Int data in the context") }
  *     val bool =
  *         cxt.use[String, Boolean] { s => println(s); true } || { false }       // when using .use, the return value must declared
  *     cxt.consume[String] { s => println(s) }                                   // you can use .consume if you don't care the return
  *                                                                               // and this will return a cxt.ConsumeResult[Any]
  *     val cxtResultOpt =                                                        // use toOption if you do not want fallback calculation
  *         cxt.use[Int, String](int => s"int: $int").toOption                    // this returns Option[String]
  *     val cxtResultOpt2 =
  *         cxt >> { (int: Int) => s"int: $int" } |?                              // this returns Option[String] too
  *     //  cxt >> { (int: Int) => cxt >> { (str: String) => { str + int } } } |? // this below is not good to use due to .flatUse
  *                                                                               // is not supported yet. It will return a
  *                                                                               // cxt.ConsumeResult[Option[String]] which is very bad
  *
  *     try {                                                // for now, you can use this way to use multiple data
  *         val int = cxt.use[Int]                           // this returns CxtOption[Int] which is Either[ContextNotGivenException, Int]
  *             .toTry.get
  *         val str = cxt >> classOf[String] match           // this >> returns the same with the .use above
  *             case Right(s) => s
  *             case Left(err) => throw err                  // this is ContextNotGivenException
  *         val bool = cxt >!> classOf[Boolean]              // the easier way to do the above
  *     } catch case e: ContextNotGivenException =>          // if any of the above val is not available, it will catch the exception
  *         e.printStackTrace()
  * }}}
  */
class GivenContext {
	
	private type ImplicitsMap [T <: Any] = mutable.HashMap[Class[?], T]
	
	private val variables: ImplicitsMap[Any] = mutable.HashMap.empty
	private val variablesWithOwner: ImplicitsMap[ImplicitsMap[Any]] = mutable.HashMap.empty
	
	def provide [T: ClassTag] (i: T): Unit =
		variables += (classTag[T].runtimeClass -> i)
	def << [T: ClassTag] (is: (Class[T], T)): Unit =
		val (_, i) = is
		this.provide[T](i)
	def << [T: ClassTag] (i: T): Unit =
		this.provide[T](i)
	
	private type CxtOption[T] = Either[ContextNotGivenException, T]
	def use [T: ClassTag]: CxtOption[T] =
		given t: RequestItemClass = RequestItemClass(classTag[T].runtimeClass)
		variables get t.clazz match
			case Some(i) => Right(i.asInstanceOf[T])
			case None => Left(ContextNotGivenException())
	def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T] match
			case Left(_) => ConsumeFailed[U]()
			case Right(i) => ConsumeSucceed[U](consumer(i))
	def >> [T: ClassTag] (t: Class[T]): CxtOption[T] =
		this.use[T]
	def >!> [T: ClassTag] (t: Class[T]): T =
		this.use[T].toTry.get
	def >>[T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
		this.use[T,U](consumer)
	def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
		this.use[T,Any](consume)
	
	@targetName("ownedBy")
	def / [O: ClassTag] (owner: O): OwnedContext[O] =
		OwnedContext[O]()
	def ownedBy [O: ClassTag]: OwnedContext[O] =
		OwnedContext[O]()
	
	class OwnedContext [O: ClassTag] {
		
		def provide [T: ClassTag] (i: T): Unit =
			(variablesWithOwner getOrElseUpdate (classTag[O].runtimeClass, mutable.HashMap.empty))
				.addOne(classTag[T].runtimeClass -> i)
		def << [T: ClassTag] (is: (Class[T], T)): Unit =
			val (_, i) = is
			this.provide[T](i)
		def << [T: ClassTag] (i: T): Unit =
			this.provide[T](i)
		
		def use [T: ClassTag]: CxtOption[T] =
			given t: RequestItemClass = RequestItemClass(classTag[T].runtimeClass)
			given u: FolderClass = FolderClass(Some(classTag[O].runtimeClass))
			variablesWithOwner get u.clazz.get match
				case Some(varColl) => varColl get t.clazz match
					case Some(i) => Right(i.asInstanceOf[T])
					case None => Left(ContextNotGivenException())
				case None => Left(ContextNotGivenException())
		def use [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			use[T] match
				case Left(_) => ConsumeFailed[U]()
				case Right(i) => ConsumeSucceed[U](consumer(i))
		def >> [T: ClassTag] (t: Class[T]): CxtOption[T] =
			this.use[T]
		def >!> [T: ClassTag] (t: Class[T]): T =
			this.use[T].toTry.get
		def >> [T: ClassTag, U] (consumer: T => U): ConsumeResult[U] =
			this.use[T,U](consumer)
		def consume [T: ClassTag] (consume: T => Any): ConsumeResult[Any] =
			this.use[T,Any](consume)
		
	}
	
	trait ConsumeResult[U]:
		def toOption: Option[U]
		def |? : Option[U] = toOption
		@targetName("orElse")
		def || (processor: =>U): U
	private class ConsumeSucceed[U] (succeedValue: U) extends ConsumeResult[U]:
		override def toOption: Option[U] = Some(succeedValue)
		@targetName("orElse")
		override def || (processor: => U): U = succeedValue
	private class ConsumeFailed[U] extends ConsumeResult[U]:
		override def toOption: Option[U] = None
		@targetName("orElse")
		override def || (processor: => U): U = processor
	
}
