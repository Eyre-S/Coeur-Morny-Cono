package cc.sukazyo.cono.morny.tele_utils

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.tele_utils.event_hack.{CommandEventHack, EventHacker, HackerEventHandler}
import cc.sukazyo.cono.morny.tele_utils.user_info.{CommandGetUser, InlineMyInformation}

class ModuleTeleUtils extends MornyInternalModule {
	
	override val id: String = "morny.tele_utils"
	override val name: String = "Morny Tele Utils : EventHack, User data, and more"
	override val description: String | Null =
		// language=markdown
		"""Added a serial tools for developing easier on Telegram.
		  |
		  |- `/event_hack`: Get the next event's raw serial data of you.
		  |- `/user`: Get user metadata for you or your roommate or by id.
		  |    Also supported to get user's DC.
		  |""".stripMargin
	
	override def onInitializingPre (using MornyCoeur)(cxt: MornyCoeur.OnInitializingPreContext): Unit = {
		import cxt.*
		
		given hacker: EventHacker = EventHacker()
		
		externalContext << hacker
		givenCxt << hacker
		
	}
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		given EventHacker = externalContext >!> classOf[EventHacker]
		
		eventManager register HackerEventHandler()
		commandManager register CommandEventHack()
		
		commandManager register CommandGetUser()
		queryManager register InlineMyInformation()
		
		queryManager register InlineRawText()
		
	}
	
}
