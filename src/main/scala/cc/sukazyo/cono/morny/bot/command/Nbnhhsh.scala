package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.{NbnhhshQuery, TelegramStickers}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}

import java.io.IOException
import scala.language.postfixOps

object Nbnhhsh extends ITelegramCommand {
	
	private val NBNHHSH_RESULT_HEAD_HTML = "<a href=\"https://lab.magiconch.com/nbnhhsh/\">## Result of nbnhhsh query :</a>"
	
	override def getName: String = "nbnhhsh"
	override def getAliases: Array[String]|Null = null
	override def getParamRule: String = "[text]"
	override def getDescription: String = "检索文本内 nbnhhsh 词条"
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		val queryTarget: String|Null =
			import cc.sukazyo.cono.morny.util.CommonConvert.stringsConnecting
			if (event.message.replyToMessage != null && event.message.replyToMessage.text != null)
				event.message.replyToMessage.text
			else if command hasArgs then
				stringsConnecting(command.getArgs, " ", 0, command.getArgs.length-1)
			else null
		
		if (queryTarget == null)
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_404
			).replyToMessageId(event.message.messageId)
			return;
		
		try {
			
			val queryResp = NbnhhshQuery sendGuess queryTarget
			
			val message = StringBuilder(NBNHHSH_RESULT_HEAD_HTML)
			
			import cc.sukazyo.cono.morny.Log.logger
			logger debug s"**xx len=${queryResp.words.length}"
			for (_word <- queryResp.words) {
				logger debug "**exec"
				if ((_word.trans ne null) && (_word.trans isEmpty)) _word.trans = null
				if ((_word.inputting ne null) && (_word.inputting isEmpty)) _word.inputting = null
				if ((_word.trans ne null) || (_word.inputting ne null))
					message ++= s"\n\n<b>[[ ${h(_word.name)} ]]</b>"
					logger debug s"**used [${_word.name}]"
					if (_word.trans != null) for (_trans <- _word.trans)
						message ++= s"\n* <i>${h(_trans)}</i>"
						logger debug s"**used [${_word.name}] used `${_trans}``"
					if (_word.inputting != null)
						logger debug s"**used [${_word.name}] inputting"
						if (_word.trans != null)
							message += '\n'
						message ++= " maybe:"
						for (_inputting <- _word.inputting)
							logger debug s"**used [${_word.name}] used-i ${_inputting}"
							message ++= s"\n` <i>${h(_inputting)}</i>"
				logger debug s"**exec as ${_word.name}"
			}
			
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				message toString
			).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
			
		} catch case e: IOException => {
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				s"""[Exception] in query:
				   |${h(e.getMessage)}
				   |""".stripMargin
			)
		}
		
	}
	
}
