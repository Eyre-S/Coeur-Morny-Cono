package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.internal.MornyInternalModule

class ModuleUniMeow extends MornyInternalModule {
	
	override val id: String = "coeur.uni_meow"
	override val name: String = "Coeur Uni-Meow Commands"
	override val description: String | Null =
		// language=Markdown
		"""Provides support for unicode command in Telegram. Also provided some
		  |interesting commands.
		  |
		  |Requires *Group Privacy* set to `disabled` on the bot account.""".stripMargin
	
	val uni_commands: UniMeowCommandManager = UniMeowCommandManager()
	
	override def onInitializingPre (using MornyCoeur)(cxt: MornyCoeur.OnInitializingPreContext): Unit = {
		import cxt.*
		externalContext << uni_commands
		givenCxt << uni_commands
	}
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		eventManager.register(
			BotEventUniMeowTrigger(using uni_commands)
		)
		
		//noinspection NonAsciiCharacters
		val $喵呜 = 喵呜()
		//noinspection NonAsciiCharacters
		uni_commands.register(
			$喵呜.抱抱,
			$喵呜.揉揉,
			$喵呜.贴贴,
			$喵呜.蹭蹭
		)
		//noinspection NonAsciiCharacters
		commandManager.register(
			$喵呜.Progynova,
			私わね(),
			创().Chuang
		)
		
	}
	
}
