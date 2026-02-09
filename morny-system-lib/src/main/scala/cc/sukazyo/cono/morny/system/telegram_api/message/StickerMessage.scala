package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.objects.ClientMedia
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendSticker}

sealed trait StickerMessage
	extends Message with SendableMessage[SendSticker] {
	
	def sticker: ClientMedia
	
	override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendSticker] = {
		sticker match {
			case idBased: ClientMedia.IDBased =>
				SendSticker(this.chat.id, idBased.fileId)
			case fileBased: ClientMedia.FileBased =>
				SendSticker(this.chat.id, fileBased.file)
			case byteArrayBased: ClientMedia.ByteArrayBased =>
				SendSticker(this.chat.id, byteArrayBased.byteArray)
		}
	}
	
}

object StickerMessage {
	
	class ClientStickerMessage (
		override val chat: Chat,
		override val replyParameters: Option[ReplyParameters],
		override val sticker: ClientMedia
	) extends StickerMessage
	
	trait CreateOps {
		this: Message =>
		
		def sticker (stickerId: String): ClientStickerMessage =
			ClientStickerMessage(this.chat, this.replyParameters, ClientMedia(stickerId))
		
	}
	
}
