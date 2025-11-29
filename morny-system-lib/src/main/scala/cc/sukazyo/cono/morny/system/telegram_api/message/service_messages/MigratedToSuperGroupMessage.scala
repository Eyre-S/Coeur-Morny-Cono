package cc.sukazyo.cono.morny.system.telegram_api.message.service_messages

import cc.sukazyo.cono.morny.system.telegram_api.message.Message

trait MigratedToSuperGroupMessage (
	newChatID: Long
) extends Message
