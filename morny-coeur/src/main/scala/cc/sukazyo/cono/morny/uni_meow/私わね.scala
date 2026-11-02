package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ISimpleCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.util.UseMath.over
import cc.sukazyo.cono.morny.util.UseRandom.*
import com.pengrad.telegrambot.model.Update

import scala.language.implicitConversions

//noinspection NonAsciiCharacters
class 私わね (using coeur: MornyCoeur) extends ISimpleCommand {
	import coeur.dsl.given
	
	override val name: String = "me"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		if ((1 over 521) chance_is true) {
			val text = "/打假"
			Messages.derive(event.message)(text)
				.send
		}
		
	}
	
}
