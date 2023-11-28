package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.extra.NbnhhshQuery
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}
import sttp.client3.{HttpError, SttpClientException}

import java.io.IOException
import scala.language.postfixOps

class Nbnhhsh (using coeur: MornyCoeur) extends ITelegramCommand {
	
	private val NBNHHSH_RESULT_HEAD_HTML =
		// language=html
		"<a href=\"https://lab.magiconch.com/nbnhhsh/\">## Result of nbnhhsh query :</a>"
	
	override val name: String = "nbnhhsh"
	override val aliases: Array[ICommandAlias]|Null = null
	override val paramRule: String = "[text]"
	override val description: String = "检索文本内 nbnhhsh 词条"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val queryTarget: String =
			if command.args nonEmpty then
				command.args mkString " "
			else if (event.message.replyToMessage != null && event.message.replyToMessage.text != null)
				event.message.replyToMessage.text
			else
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_404
				).replyToMessageId(event.message.messageId)
				return;
		
		try {
			
			val queryResp = NbnhhshQuery sendGuess queryTarget
			
			val message = StringBuilder(NBNHHSH_RESULT_HEAD_HTML)
			
			import cc.sukazyo.cono.morny.Log.logger
			logger trace s"**nbnhhsh got len=${queryResp.words.length}"
			for (_word <- queryResp.words) {
				logger trace s"**start for ${_word.name}"
				val _use_trans = (_word.trans ne null) && (_word.trans nonEmpty)
				val _use_inputting = (_word.inputting ne null) && (_word.inputting nonEmpty)
				if (_use_trans || _use_inputting)
					message ++= s"\n\n<b>[[ ${h(_word.name)} ]]</b>"
					logger trace s"**used [${_word.name}]"
					if (_use_trans) for (_trans <- _word.trans)
						message ++= s"\n* <i>${h(_trans)}</i>"
						logger trace s"**used [${_word.name}] used `${_trans}``"
					if (_use_inputting)
						logger trace s"**used [${_word.name}] inputting"
						if (_use_trans)
							message += '\n'
						message ++= " maybe:"
						for (_inputting <- _word.inputting)
							logger trace s"**used [${_word.name}] used-i ${_inputting}"
							message ++= s"\n` <i>${h(_inputting)}</i>"
				logger trace s"**done"
			}
			
			coeur.account exec SendMessage(
				event.message.chat.id,
				message toString
			).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
			
		} catch case e: (HttpError[_] | SttpClientException) => {
			coeur.account exec SendMessage(
				event.message.chat.id,
				s"""[Exception] in query:
				   |${h(e.getMessage)}
				   |""".stripMargin
			)
		}
		
	}
	
}
