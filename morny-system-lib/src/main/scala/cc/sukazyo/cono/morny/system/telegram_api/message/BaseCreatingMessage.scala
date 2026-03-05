package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageID
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

/** A message builder helper with common parameters.
  *
  * ## parameters inside
  *
  * It contains the following common parameters:
  *
  * - target [[Chat]] (with [[cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageThreadID]]).
  *   Immutable.
  * - [[ReplyParameters]], maybe reply, or cross-chat reference. Can be changed by [[replyTo]]
  *   and [[noReply]].
  *
  * Every change to the parameters will return a new instance of [[BaseCreatingMessage]].
  *
  * ## create a more specific message
  *
  * You can use methods within the following trait to make this base message builder into a
  * specific message object:
  *
  * - [[TextMessage.CreateOps]] for text message.
  * - [[StickerMessage.CreateOps]] for sticker message.
  * - media group messages, notice that every type exclude [[MediaGroupMessage.CreateOps]] can
  *   contain only one media file.
  *   - [[MediaGroupMessage.CreateOps]] for media group message. Only this can contain two or
  *     more medias, and the type of media is not limited.
  *   - [[PhotoMessage.CreateOps]] for photo message.
  *
  * @since 2.0.0-alpha22
  */
class BaseCreatingMessage (
	override val chat: Chat,
	override val replyParameters: Option[ReplyParameters]
) extends Message
	with TextMessage.CreateOps
	with StickerMessage.CreateOps
	with PhotoMessage.CreateOps
	with MediaGroupMessage.CreateOps {
	
	/** Create a new [[BaseCreatingMessage]] with the given [[ReplyParameters]].
	  * @since 2.0.0-alpha22
	  */
	def replyTo (replyParameters: ReplyParameters): BaseCreatingMessage =
		BaseCreatingMessage(chat, Some(replyParameters))
	
	/** Create a new [[BaseCreatingMessage]] without any reply.
	  * @since 2.0.0-alpha22
	  */
	def noReply: BaseCreatingMessage =
		BaseCreatingMessage(chat, None)
	
	/** Create a new [[BaseCreatingMessage]] with the given [[ReplyParameters]].
	  *
	  * @since 2.0.0-alpha22
	  */
	def replyTo (messageId: MessageID): BaseCreatingMessage =
		this.replyTo(ReplyParameters(messageId))
	
}
