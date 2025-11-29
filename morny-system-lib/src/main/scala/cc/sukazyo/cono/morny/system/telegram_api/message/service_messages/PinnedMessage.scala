package cc.sukazyo.cono.morny.system.telegram_api.message.service_messages

import cc.sukazyo.cono.morny.system.telegram_api.message.Message
import cc.sukazyo.cono.morny.system.telegram_api.message.server_message.MaybeServerMessage

trait PinnedMessage (
	pinnedMessage: MaybeServerMessage
) extends Message
