package cc.sukazyo.cono.morny.core.bot.api.messages

import com.pengrad.telegrambot.request.AbstractSendRequest

/** A error message based on Telegram's [[AbstractSendRequest]].
  *
  * Contains two types ([[simple]] and [[complex]]) message that
  * allows to choose one to show in different cases.
  *
  * Also there contains a [[context]] (typed with [[MessagingContext.WithMessage]])
  * that infers the source of this error message, also can be used for
  * the unique key of one error message.
  *
  * There's also a [[ErrorMessage.Types]] enum infers to the two type. You
  * can use [[getByType]] (or [[getByTypeNormal]]) method to get the specific
  * type's message of that type.
  *
  * @since 2.0.0
  *
  * @see [[cc.sukazyo.cono.morny.core.bot.internal.ErrorMessageManager]] This
  *      ErrorMessage's manager (consumer) implementation by the Morny Coeur.
  * @see [[cc.sukazyo.cono.morny.core.bot.api.BotExtension.submit]] Simple
  *      way to consume this.
  *
  * @tparam T1 Type of the simple error message
  * @tparam T2 Type of the complex error message
  */
trait ErrorMessage[T1 <: AbstractSendRequest[T1], T2 <: AbstractSendRequest[T2]] {
	
	/** The simple variant of this error message.
	  * 
	  * In Morny, this is usually a sticker that shows the generic type of the error. 
	  *
	  * Can be get by using [[getByType]] with variant tag [[ErrorMessage.Types.Simple]]
	  * @since 2.0.0
	  */
	val simple: T1
	/** The complex variant of this error message.
	  *
	  * In Morny, this should be a text-based message that shows key information of the error
	  * which want to describe.
	  *
	  * Can be get by using [[getByType]] with variant tag [[ErrorMessage.Types.Complex]]
	  * @since 2.0.0
	  */
	val complex: T2
	
	/** An context that defines where this error message belongs. Will be used to publish this
	  * message (aka. send message).
	  * @since 2.0.0
	  */
	val context: MessagingContext.WithMessage
	
	/** Get the simple or complex message by the given [[ErrorMessage.Types]] infer.
	  *
	  * This method returns a union type [[T1]] and [[T2]]. This may be
	  * not useful when you don't care about the specific return types (maybe for
	  * the most times). You can use [[getByTypeNormal]] instead.
	  *
	  * @since 2.0.0
	  */
	def getByType (t: ErrorMessage.Types): AbstractSendRequest[T1] | AbstractSendRequest[T2] =
		t match
			case ErrorMessage.Types.Simple => simple
			case ErrorMessage.Types.Complex => complex
	
	/** Get the simple or complex message by the given [[ErrorMessage.Types]] infer.
	  *
	  * This works exactly the same with the [[getByType]], the only difference is
	  * this returns with a bit universal type `AbstractSendRequest[?]`.
	  *
	  * @see [[getByType]]
	  * @since 2.0.0
	  */
	def getByTypeNormal (t: ErrorMessage.Types): AbstractSendRequest[?] =
		getByType(t)
	
}

/** @see [[ErrorMessage]] */
object ErrorMessage {
	
	/** An enum that contains the tag of an [[ErrorMessage]]'s variant type.
	  *
	  * Can be used to get the specific type's message of that type using [[ErrorMessage.getByType]].
	  *
	  * @since 2.0.0
	  */
	enum Types:
		/** @see [[ErrorMessage.simple]] */
		case Simple
		/** @see [[ErrorMessage.complex]] */
		case Complex
	
	/** A new [[ErrorMessage]].
	  *
	  * @since 2.0.0
	  * @see [[ErrorMessage]]
	  *
	  * @param _simple The [[ErrorMessage.simple]] message of this error message.
	  * @param _complex The [[ErrorMessage.complex]] message of this error message.
	  * @param cxt The context of this error message, defines where the error message belongs.
	  */
	def apply [T1 <: AbstractSendRequest[T1], T2<: AbstractSendRequest[T2]]
	(_simple: T1, _complex: T2)(using cxt: MessagingContext.WithMessage): ErrorMessage[T1, T2] =
		new ErrorMessage[T1, T2]:
			override val simple: T1 = _simple
			override val complex: T2 = _complex
			override val context: MessagingContext.WithMessage = cxt
	
}
