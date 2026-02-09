package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.objects.{ClientMedia, ClientRichMedia}
import cc.sukazyo.cono.morny.system.telegram_api.text.{MessageText, NativeText}
import com.pengrad.telegrambot.model.request.{ParseMode, ReplyParameters}
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendPhoto}

sealed trait PhotoMessage
	extends Message with SendableMessage[SendPhoto] {
	
	def media: List[ClientRichMedia]
	
	@throws[UnsupportedForSendException]
	override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendPhoto] = {
		if (media.length != 1)
			throw UnsupportedForSendException()
		
		val request = media.head.media match
			case idBased: ClientMedia.IDBased =>
				SendPhoto(this.chat.id, idBased.fileId)
			case fileBased: ClientMedia.FileBased =>
				SendPhoto(this.chat.id, fileBased.file)
			case bytesBased: ClientMedia.ByteArrayBased =>
				SendPhoto(this.chat.id, bytesBased.byteArray)
		
		media.head.caption.map { caption =>
			val message = caption.compile
			request.caption(message.message)
			message.parseMode.map(request.parseMode)
			if (message.entities.nonEmpty)
				request.captionEntities(message.entities*)
		}
		
		request
		
	}
	
}

object PhotoMessage {
	
	private class PhotoMessageImpl (
		val chat: Chat,
		val replyParameters: Option[ReplyParameters],
		val media: List[ClientRichMedia]
	) extends PhotoMessage
	
	trait CreateOps {
		this: Message =>
		
		def photo (medias: List[ClientRichMedia]): PhotoMessage = {
			if (medias.isEmpty)
				throw IllegalArgumentException("PhotoMessage cannot contains medias that less than 1 media.")
			PhotoMessageImpl(this.chat, this.replyParameters, medias)
		}
		
		def photo (media: ClientRichMedia): PhotoMessage = {
			this.photo(media :: Nil)
		}
		
		def photo (mediaId: String, caption: MessageText): PhotoMessage = {
			this.photo(ClientRichMedia(ClientMedia(mediaId), caption))
		}
		
		def photo (mediaId: String, caption: String, parseMode: ParseMode): PhotoMessage = {
			this.photo(mediaId, NativeText(caption, Some(parseMode), Nil))
		}
		
	}
	
}
