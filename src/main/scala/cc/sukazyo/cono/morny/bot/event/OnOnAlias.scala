package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Chat

class OnOnAlias (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		
		if (event.update.message.chat.`type` != Chat.Type.Private) return;
		if !(List("o", "/o") contains event.update.message.text) then return;
		
		coeur.commands.$MornyHellos.On.execute(using InputCommand(""), event.update)
		
	}
	
}
