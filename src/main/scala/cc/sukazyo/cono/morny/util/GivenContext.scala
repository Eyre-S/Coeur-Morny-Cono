package cc.sukazyo.cono.morny.util

import scala.annotation.targetName
import scala.collection.mutable
import scala.reflect.{classTag, ClassTag}

class GivenContext {
	
	private type ImplicitsMap [T <: Any] = mutable.HashMap[Class[?], T]
	
	private val variables: ImplicitsMap[Any] = mutable.HashMap.empty
	private val variablesWithOwner: ImplicitsMap[ImplicitsMap[Any]] = mutable.HashMap.empty
	
	def provide (i: Any): Unit =
		variables += (i.getClass -> i)
	def << (i: Any): Unit =
		this.provide(i)
	
	def >>[T: ClassTag] (consumer: T => Unit): ConsumeResult =
		this.use[T](consumer)
	def use [T: ClassTag] (consumer: T => Unit): ConsumeResult =
		variables get classTag[T].runtimeClass match
			case Some(i) => consumer(i.asInstanceOf[T]); ConsumeResult(true)
			case None => ConsumeResult(false)
	def consume [T: ClassTag] (consume: T => Unit): ConsumeResult =
		this.use[T](consume)
	
	@targetName("ownedBy")
	def / [O: ClassTag] (owner: O): OwnedContext[O] =
		OwnedContext[O]()
	def ownedBy [O: ClassTag]: OwnedContext[O] =
		OwnedContext[O]()
	
	class OwnedContext [O: ClassTag] {
		
		def provide (i: Any): Unit =
			(variablesWithOwner getOrElseUpdate (classTag[O].runtimeClass, mutable.HashMap.empty))
				.addOne(i.getClass -> i)
		def << (i: Any): Unit =
			this.provide(i)
		
		def >> [T: ClassTag] (consumer: T => Unit): ConsumeResult =
			this.use[T](consumer)
		def use [T: ClassTag] (consumer: T => Unit): ConsumeResult =
			variablesWithOwner(classTag[O].runtimeClass) get classTag[T].runtimeClass match
				case Some(i) => consumer(i.asInstanceOf[T]); ConsumeResult(true)
				case None => ConsumeResult(false)
		
	}
	
	class ConsumeResult (success: Boolean) {
		@targetName("orElse")
		def || (processor: => Unit): Unit = {
			if !success then processor
		}
	}
	
}
