package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageID
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

class BaseCreatingMessage (
	override val chat: Chat,
	override val replyParameters: Option[ReplyParameters]
) extends Message
	with TextMessage.CreateOps
	with StickerMessage.CreateOps {
	
	def replyTo (replyParameters: ReplyParameters): BaseCreatingMessage =
		BaseCreatingMessage(chat, Some(replyParameters))
	
	def noReply: BaseCreatingMessage =
		BaseCreatingMessage(chat, None)
	
	def replyTo (messageId: MessageID): BaseCreatingMessage =
		this.replyTo(ReplyParameters(messageId))
	
}
