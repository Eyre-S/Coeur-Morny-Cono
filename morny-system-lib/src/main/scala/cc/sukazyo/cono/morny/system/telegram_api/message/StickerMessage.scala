package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSimpleSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.objects.ClientMediaData
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.SendSticker
import com.pengrad.telegrambot.response.SendResponse

sealed trait StickerMessage
	extends Message with SendableMessage [NativeSimpleSendRequest[SendSticker], SendSticker, SendResponse] {
	
	def sticker: ClientMediaData
	
	override def generateBaseSendRequest (sendContext: SendMessageContext): NativeSimpleSendRequest[SendSticker] = {
		NativeSimpleSendRequest(sticker match {
			case idBased: ClientMediaData.IDBased =>
				SendSticker(this.chat.id, idBased.fileId)
			case fileBased: ClientMediaData.FileBased =>
				SendSticker(this.chat.id, fileBased.file)
			case byteArrayBased: ClientMediaData.ByteArrayBased =>
				SendSticker(this.chat.id, byteArrayBased.byteArray)
		})
	}
	
}

object StickerMessage {
	
	class ClientStickerMessage (
		override val chat: Chat,
		override val replyParameters: Option[ReplyParameters],
		override val sticker: ClientMediaData
	) extends StickerMessage
	
	trait CreateOps {
		this: Message =>
		
		def sticker (stickerId: String): ClientStickerMessage =
			ClientStickerMessage(this.chat, this.replyParameters, ClientMediaData(stickerId))
		
	}
	
}
