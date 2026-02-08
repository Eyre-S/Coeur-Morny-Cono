package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount
import cc.sukazyo.cono.morny.system.telegram_api.text.Text
import com.pengrad.telegrambot.request.SendMessage

trait TextMessage (
	
	val text: Text,
	val linkPreviewOption: Option[?],
	
) extends Message with SendableMessage
