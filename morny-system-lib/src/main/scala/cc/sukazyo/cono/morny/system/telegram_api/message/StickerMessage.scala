package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendSticker}

import java.io.File

sealed trait StickerMessage
	extends Message with SendableMessage[SendSticker]

object StickerMessage {
	
	trait FileBase extends StickerMessage {
		def file: File
		override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendSticker] =
			SendSticker(this.chat.id, file)
	}
	
	trait IDBase extends StickerMessage {
		def stickerId: String
		override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendSticker] =
			SendSticker(this.chat.id, stickerId)
	}
	
	trait ByteArrayBase extends StickerMessage {
		def byteArray: Array[Byte]
		override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendSticker] =
			SendSticker(this.chat.id, byteArray)
	}
	
	class ClientIDBasedStickerMessage (
		override val chat: Chat,
		override val replyParameters: Option[ReplyParameters],
		override val stickerId: String,
	) extends IDBase
	
	trait CreateOps {
		this: Message =>
		
		def sticker (stickerId: String): ClientIDBasedStickerMessage =
			ClientIDBasedStickerMessage(this.chat, this.replyParameters, stickerId)
		
	}
	
}
