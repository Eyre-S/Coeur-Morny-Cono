package cc.sukazyo.cono.morny.system.telegram_api.objects

import com.pengrad.telegrambot.model.request.InputMedia

/** A media that can be sent.
  * 
  * @tparam T The native type of this media, which should be a subtype of [[InputMedia]].
  */
trait AbstractClientMedia [T <: InputMedia[T]] extends AbstractCreatingMedia {
	
	/** The media type indicator.
	  *
	  * In Telegram Bot API, media will have a string formatted type to indicates different
	  * media types, such as "photo", "video", "document", etc.
	  * 
	  * This method should return one of the available media type indicators. It should also
	  * match the type [[T]] that current class is trying to implement.
	  * 
	  * Since [[InputMedia]]'s implementations have already defined this, it may not being used
	  * in most cases (cases that uses [[toNative]] directly). This exists for mostly
	  * capability consideration.
	  * 
	  * @see https://core.telegram.org/bots/api#inputmedia Original definitions about media
	  *      types.
	  */
	def mediaType: String
	
	/** Convert this client media to a Telegram Bot API's [[InputMedia]] implementation, so
	  * that native `Send` methods can be used to send this media.
	  * 
	  * <details>
	  * <summary>For implementors</summary>
	  * 
	  * You do not need to decorate the [[InputMedia]] for common properties that already
	  * exists in the extended traits. A method [[decorateNative]] is provided for this purpose.
	  * 
	  * You can, obvisously, decorate with your own properties here. You can also decorate them
	  * in the [[decorateNative]] method, that may provides better compatibility when your
	  * class is extended.
	  * 
	  * At last, don't forget to call the [[decorateNative]] method to decorate your new
	  * [[InputMedia]] at the end of this method!
	  * 
	  * </details>
	  */
	def toNative: InputMedia[T]
	
	/** Used to decorate common properties of a newly created [[InputMedia]] from [[toNative]].
	  * 
	  *
	  * <details>
	  * <summary>For implementors</summary>
	  *
	  * Don't forget to call the super method if you are overriding this method. Otherwise the
	  * common properties from extended traits will loss!
	  *
	  * </details>
	  */
	def decorateNative [T2 <: InputMedia[T2]] (native: T2): Unit = {
		this.caption.map { it =>
			val text = it.compile
			native.caption(text.message)
			text.parseMode.map(native.parseMode)
			if text.entities.nonEmpty then
				native.captionEntities(text.entities *)
		}
	}
	
}
