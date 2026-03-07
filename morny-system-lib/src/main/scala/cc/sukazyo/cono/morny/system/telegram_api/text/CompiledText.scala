package cc.sukazyo.cono.morny.system.telegram_api.text

import com.pengrad.telegrambot.model.MessageEntity
import com.pengrad.telegrambot.model.request.ParseMode

case class CompiledText (
	message: String,
	parseMode: Option[ParseMode],
	entities: Seq[MessageEntity]
)
