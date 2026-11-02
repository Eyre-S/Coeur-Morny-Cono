package cc.sukazyo.cono.morny.system.telegram_api.message.server_message

import cc.sukazyo.cono.morny.system.telegram_api.message.Message
import cc.sukazyo.cono.morny.system.telegram_api.message.sender.MessageSender
import cc.sukazyo.std.datetime.DateTimeTypeAliases.EpochSeconds

trait ServerMessage (
	
	val message: Message,
	
	val sender: MessageSender,
	val sentDate: EpochSeconds,
	val editData: EpochSeconds,
	
	val channelPostAuthorSignature: String,
	
	val senderBoostCount: Int,
	val paidStarCount: Int,
	
	val isTopicMessage: Boolean,
	val isAutomaticForward: Boolean,
	val isOfflineMessage: Boolean,
	
	val replyToMessage: Option[ServerMessage],
	val canHaveReply: Boolean,
	val externalReply: Option[?],
	val quotes: Option[?],
	val replyToStory: Option[?],
	
) extends MaybeServerMessage {
	
	def isLinkedChannelPost: this.isAutomaticForward.type = isAutomaticForward
	
}
