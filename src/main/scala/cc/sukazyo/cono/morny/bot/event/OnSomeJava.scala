package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

class OnSomeJava (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		if update.message.text eq null then return
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if (1 over 5) chance_is false then return;
		if !update.message.text.toLowerCase.contains("java") then return;
		
		coeur.account exec SendMessage(
			update.message.chat.id,
			"☕"
		).replyToMessageId(update.message.messageId)
		event.setEventOk
		
	}
	
}
