package cc.sukazyo.cono.morny.randomize_somthing

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.randomize_somthing.OnQuestionMarkReply.isAllMessageMark
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages

import scala.util.boundary
import scala.util.boundary.break

class OnQuestionMarkReply (using coeur: MornyCoeur) extends EventListener {
	import coeur.dsl.given
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		if update.message.text eq null then return
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is

		import scala.language.implicitConversions
		if 7 over 8 chance_is true then return;
		if !isAllMessageMark(using update.message.text) then return;
		
		// TODO: copy message is able to use
		Messages.derive(update.message)
			(update.message.text)
			.send
		event.setEventOk
		
	}
	
}

object OnQuestionMarkReply {
	
	// todo: due to the limitation of Java char, the ⁉️ character (actually not a
	//  single character) is not supported yet.
	private val QUESTION_MARKS = Set('?', '？', '¿', '⁈', '⁇', '‽', '⸘', '❔', '❓')
	
	def isAllMessageMark (using text: String): Boolean = {
		boundary[Boolean] {
			for (c <- text)
				if !(QUESTION_MARKS contains c) then
					break(false)
			true
		}
	}
	
}
