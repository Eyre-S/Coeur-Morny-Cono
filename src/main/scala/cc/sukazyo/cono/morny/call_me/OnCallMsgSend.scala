package cc.sukazyo.cono.morny.call_me

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.*
import com.pengrad.telegrambot.model.{Chat, Message, MessageEntity}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetChat, SendMessage, SendSticker}
import com.pengrad.telegrambot.TelegramBot

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps
import scala.util.matching.Regex

class OnCallMsgSend (using coeur: MornyCoeur) extends EventListener {
	private given TelegramBot = coeur.account
	
	private val REGEX_MSG_SENDREQ_DATA_HEAD: Regex = "^\\*msg(-?\\d+)(\\*\\S+)?(?:\\n([\\s\\S]+))?$"r
	
	case class MessageToSend (
		message: String|Null,
		entities: Array[MessageEntity]|Null,
		parseMode: ParseMode|Null,
		targetId: Long
	) {
		def toSendMessage (target_override: Long|Null = null): SendMessage =
			val useTarget = if target_override == null then targetId else target_override
			val sendMessage = SendMessage(useTarget, message)
			if entities ne null then sendMessage.entities(entities*)
			if parseMode ne null then sendMessage.parseMode(parseMode)
			sendMessage
	}
	private object MessageToSend:
		infix def from (raw: Message): MessageToSend = {
			raw.text match
				case REGEX_MSG_SENDREQ_DATA_HEAD(_target, _parseMode, _body) =>
					val target = _target toLong
					val parseMode: ParseMode | Null = _parseMode match
						case "*markdown" | "*md" | "*m↓" => ParseMode MarkdownV2
						case "*md1" => ParseMode Markdown
						case "*html" => ParseMode HTML
						case _ => null
					val bodyOffset = "*msg".length + _target.length + (if _parseMode eq null then 0 else _parseMode.length) + 1
					val entities = ArrayBuffer.empty[MessageEntity]
					if (raw.entities ne null) for (e <- raw.entities)
						val _parsed = MessageEntity(e.`type`, e.offset - bodyOffset, e.length)
						if e.url ne null then _parsed.url(e.url)
						if e.user ne null then _parsed.user(e.user)
						if e.language ne null then _parsed.language(e.language)
						if e.customEmojiId ne null then _parsed.language(e.language)
						entities += _parsed
					MessageToSend(_body, entities toArray, parseMode, target)
				case _ => null
		}
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		val message = update.message
		
		if message.chat.`type` != Chat.Type.Private then return
		if message.text eq null then return
		if !(message.text `startsWith` "*msg") then return
		
		if (!(coeur.trusted isTrust message.from))
			SendSticker(
				message.chat.id,
				TelegramStickers ID_403
			).replyToMessageId(message.messageId).unsafeExecute
			event.setEventOk
			return;
		
		if (message.text == "*msgsend") {
			
			if (message.replyToMessage eq null) { answer404; return }
			val messageToSend = MessageToSend from message.replyToMessage
			if ((messageToSend eq null) || (messageToSend.message eq null)) { answer404; return }
			val sendResponse = messageToSend.toSendMessage().execute
			
			if (sendResponse isOk) {
				SendSticker(
					update.message.chat.id,
					TelegramStickers ID_SENT
				).replyToMessageId(update.message.messageId)
					.unsafeExecute
			} else {
				SendMessage(
					update.message.chat.id,
					// language=html
					s"""<b><u>${sendResponse.errorCode} FAILED</u></b>
					   |<code>${sendResponse.description}</code>"""
					.stripMargin
				).replyToMessageId(update.message.messageId).parseMode(ParseMode HTML)
					.unsafeExecute
			}
			
			event.setEventOk
			return
			
		}
		
		val messageToSend: MessageToSend =
			val raw: Message =
				if (message.text == "*msg")
					if message.replyToMessage eq null then { answer404; return }
					else message.replyToMessage
				else if (message.text `startsWith` "*msg")
					message
				else { answer404; return }
			val _toSend = MessageToSend from raw
			if _toSend eq null then { answer404; return }
			else _toSend
		
		val targetChatResponse = GetChat(messageToSend.targetId).execute
		if (targetChatResponse isOk) {
			def getChatDescriptionHTML (chat: Chat): String =
				import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
				import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
				// language=html
				s"""<i><u>${h(chat.id toString)}</u>@${h(chat.`type`.name)}</i>${if (chat.`type` != Chat.Type.Private) ":::" else ""}
				   |${chat.typeTag} <b>${h(chat.safe_name)}</b> ${chat.safe_linkHTML}"""
				.stripMargin
			SendMessage(
				update.message.chat.id,
				getChatDescriptionHTML(targetChatResponse.chat)
			).parseMode(ParseMode HTML).replyToMessageId(update.message.messageId)
				.unsafeExecute
		} else {
			SendMessage(
				update.message.chat.id,
				// language=html
				s"""<b><u>${targetChatResponse.errorCode} FAILED</u></b>
				   |<code>${targetChatResponse.description}</code>"""
						.stripMargin
			).parseMode(ParseMode HTML).replyToMessageId(update.message.messageId)
				.unsafeExecute
		}
		
		if messageToSend.message eq null then { answer404; return }
		val testSendResponse =
			messageToSend.toSendMessage(update.message.chat.id).replyToMessageId(update.message.messageId)
				.execute
		if (!(testSendResponse isOk))
			SendMessage(
				update.message.chat.id,
				// language=html
				s"""<b><u>${testSendResponse.errorCode}</u> FAILED</b>
				   |<code>${testSendResponse.description}</code>"""
				.stripMargin
			).parseMode(ParseMode HTML).replyToMessageId(update.message.messageId)
				.unsafeExecute
		
		event.setEventOk
		
	}
	
	private def answer404 (using event: EventEnv): Unit =
		SendSticker(
			event.update.message.chat.id,
			TelegramStickers ID_404
		).replyToMessageId(event.update.message.messageId)
			.unsafeExecute
		event.setEventOk
	
}
