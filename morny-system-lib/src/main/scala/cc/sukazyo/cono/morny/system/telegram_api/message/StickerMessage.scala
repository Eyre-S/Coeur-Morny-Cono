package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendSticker}

trait StickerMessage (
	sticker: Null
) extends Message with SendableMessage[SendSticker] {
	
	override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendSticker] =
		???
	
}
