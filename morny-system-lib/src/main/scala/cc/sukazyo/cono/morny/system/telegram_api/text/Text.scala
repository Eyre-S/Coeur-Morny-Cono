package cc.sukazyo.cono.morny.system.telegram_api.text

import cc.sukazyo.cono.morny.system.telegram_api.text.Text.CompiledText
import com.pengrad.telegrambot.model.MessageEntity

object Text {
	
	type CompiledText = (text: String, entities: List[MessageEntity])
	
}

trait Text {
	
	def compile: CompiledText
	
}
