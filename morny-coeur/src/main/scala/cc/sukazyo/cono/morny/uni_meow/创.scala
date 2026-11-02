package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ISimpleCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.{MessageEntity, Update}

//noinspection NonAsciiCharacters
class 创 (using coeur: MornyCoeur) {
	private given TelegramBot = coeur.account
	import coeur.dsl.given
	
	object Chuang extends ISimpleCommand {
		
		override val name: String = "chuang"
		override val aliases: List[ICommandAlias] = Nil
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			val text =
				if (command.args nonEmpty)
					command.args mkString " "
				else if ((event.message.replyToMessage ne null) && (event.message.replyToMessage.text ne null))
					event.message.replyToMessage.text
				else
					// this will directly return for not conflict with @autoziyaobot
					return;
			
			val chuangText = 创.chuangText(text)
			Messages.derive(event.message)(
					Texts.plain(chuangText)
						.withEntities(MessageEntity(MessageEntity.Type.pre, 0, chuangText.length))
				).send
			
		}
		
	}
	
}

//noinspection NonAsciiCharacters
object 创 {
	
	def chuangText (text: String): String = {
		
		val c = StringBuilder()
		
		import cc.sukazyo.cono.morny.util.StringEnsure.ensureSize

		import Math.*
		val lines = text split '\n'
		val _base = max(5, lines.map(l => l.length).max)
		val _min = 5
		val _ext = _base - _min
		c         ++= raw"     " ++= " " * _base                  ++= raw"     \ |     " += '\n'
		c         ++= raw"     " ++= " " * _base                  ++= raw"     -+-     " += '\n'
		c         ++= raw" +-+-" ++= "-" * _base                  ++= raw"-+-\   O     " += '\n'
		c         ++= raw" | | " ++= " " * _base                  ++= raw" || \        " += '\n'
		if (lines.length > 1)
			c     ++= raw" | | " ++= lines(0).ensureSize(_base)   ++= raw" ||  \       " += '\n'
			for (l <- lines drop 1 dropRight 1)
				c ++= raw" | | " ++= l.ensureSize(_base)          ++= raw" ||   |      " += '\n'
			c     ++= raw" | | " ++= lines.last.ensureSize(_base) ++= raw" ||   |   O  " += '\n'
		else
			c     ++= raw" | | " ++= lines(0).ensureSize(_base)   ++= raw" ||  \    O  " += '\n'
		c         ++= raw" | | " ++= " " * _base                  ++= raw" ||   |  -+- " += '\n'
		c         ++= raw" +-+-" ++= raw"-/-\-${"-" * _ext}"      ++= raw"-++/-\+ _/ \ " += '\n'
		c         ++= raw"     " ++= raw" \_/ ${" " * _ext}"      ++= raw"   \-/       " += '\n'
		
		c.toString
		
	}
	
}
