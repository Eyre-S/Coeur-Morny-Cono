package cc.sukazyo.cono.morny.system.telegram_api.text

import com.pengrad.telegrambot.model.MessageEntity
import com.pengrad.telegrambot.model.request.ParseMode

class NativeText (
	val message: String,
	val parseMode: Option[ParseMode],
	val entities: Seq[MessageEntity]
) extends MessageText {
	
	override def compile: CompiledText =
		CompiledText(message, parseMode, entities)
	
	def withEntities (entities: MessageEntity*): NativeText = {
		NativeText(message, parseMode, entities)
	}
	
}

object NativeText {
	
	def plain (message: String): NativeText =
		NativeText(message, None, Nil)
	
	def html (message: String): NativeText =
		NativeText(message, Some(ParseMode.HTML), Nil)
	
	def markdownV2 (message: String): NativeText =
		NativeText(message, Some(ParseMode.MarkdownV2), Nil)
	
	def markdown (message: String): NativeText =
		NativeText(message, Some(ParseMode.Markdown), Nil)
	
}
