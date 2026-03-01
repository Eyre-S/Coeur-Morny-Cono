package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSimpleSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.objects.{ClientMediaData, ClientPhotoMedia}
import cc.sukazyo.cono.morny.system.telegram_api.text.{MessageText, NativeText}
import com.pengrad.telegrambot.model.request.{ParseMode, ReplyParameters}
import com.pengrad.telegrambot.request.SendPhoto
import com.pengrad.telegrambot.response.SendResponse

sealed trait PhotoMessage
	extends MediaMessage[NativeSimpleSendRequest[SendPhoto], SendPhoto, SendResponse] {
	
	def media: ClientPhotoMedia
	
	@throws[UnsupportedForSendException]
	override def getSendRequest (sendContext: SendMessageContext): NativeSimpleSendRequest[SendPhoto] = {
		
		val request = media.mediaData match
			case idBased: ClientMediaData.IDBased =>
				SendPhoto(this.chat.id, idBased.fileId)
			case fileBased: ClientMediaData.FileBased =>
				SendPhoto(this.chat.id, fileBased.file)
			case bytesBased: ClientMediaData.ByteArrayBased =>
				SendPhoto(this.chat.id, bytesBased.byteArray)
		
		media.caption.map { caption =>
			val message = caption.compile
			request.caption(message.message)
			message.parseMode.map(request.parseMode)
			if (message.entities.nonEmpty)
				request.captionEntities(message.entities*)
		}
		
		NativeSimpleSendRequest(request)
		
	}
	
}

object PhotoMessage {
	
	private class PhotoMessageImpl (
		val chat: Chat,
		val replyParameters: Option[ReplyParameters],
		val media: ClientPhotoMedia
	) extends PhotoMessage
	
	trait CreateOps {
		this: Message =>
		
		def photo (media: ClientPhotoMedia): PhotoMessage = {
			PhotoMessageImpl(this.chat, this.replyParameters, media)
		}
		
		def media (media: ClientPhotoMedia): PhotoMessage =
			this.photo(media)
		
		def photo (mediaId: String, caption: MessageText): PhotoMessage = {
			this.photo(ClientPhotoMedia(ClientMediaData(mediaId), Some(caption)))
		}
		
		def photo (mediaId: String, caption: String, parseMode: ParseMode): PhotoMessage = {
			this.photo(mediaId, NativeText(caption, Some(parseMode), Nil))
		}
		
	}
	
}
