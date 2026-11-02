package cc.sukazyo.cono.morny.tele_utils.event_hack

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable

class EventHacker (using coeur: MornyCoeur) {
	import coeur.dsl.given
	
	private case class Hacker (from_chat: Long, from_message: Long):
		override def toString: String = s"$from_chat/$from_message"
	
	enum HackType:
		case USER
		case GROUP
		case ANY
	
	private val hackers = mutable.HashMap.empty[String, Hacker]
	
	def registerHack (from_message: Long, from_user: Long, from_chat: Long, t: HackType): Unit =
		val record = t match
			case HackType.USER => s"(($from_user))"
			case HackType.GROUP => s"{{$from_chat}}"
			case HackType.ANY => "[[]]"
		hackers += (record -> Hacker(from_chat, from_message))
		logger `debug` s"add hacker track $record"
	
	def trigger (chat: Long, fromUser: Long)(using update: Update): Boolean = {
		logger `debug` s"got event signed {{$chat}}(($fromUser))"
		val x: Hacker =
			if hackers contains s"(($fromUser))" then (hackers remove s"(($fromUser))") get
			else if hackers contains s"{{$chat}}" then (hackers remove s"{{$chat}}") get
			else if hackers contains "[[]]" then (hackers remove "[[]]") get
			else return false
		logger `debug` s"hacked event by $x"
		import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
		// TODO: add MessageThread support
		Messages.create(x.from_chat).replyTo(x.from_message.toInt)(Texts.html(
			// language=html
			s"<pre><code class='language-json'>${h(GsonBuilder().setPrettyPrinting().create.toJson(update))}</code></pre>"
		)).send
		true
	}
	
}
