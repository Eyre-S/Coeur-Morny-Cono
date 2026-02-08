package cc.sukazyo.cono.morny.system.telegram_api.message.server_message

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageGroupID

trait GroupedServerMessage (
	val messageGroupId: MessageGroupID
) extends ServerMessage
