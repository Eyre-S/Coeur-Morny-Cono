package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount

/** This message type is able to send via
  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage()]].
  */
trait SendableMessage extends Message
