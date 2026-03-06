package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest

trait MaybeSendableMessage {
	
	/** Decorate the newly generated [[NativeSendRequest]] with additional parameters.
	  *
	  * If an abstract layer of message provides extra parameters, that parameters can be set
	  * to the send request in this method.
	  *
	  * > since requests object from Telegram Bot API are mutable, this method can directly
	  * > modify the request object without returning a new one. So that this method is a `Unit`
	  * > method. It also makes this method not necessary be a generic method.
	  *
	  * ## Implementation note
	  *
	  * Don't forget to call the super method, to ensure the basic parameters are set.
	  *
	  * @since 2.0.0-alpha22
	  */
	def decorateSendRequest (request: NativeSendRequest[?, ?]): Unit
	
}
