package cc.sukazyo.cono.morny.slash_action

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.MornyCoeur

class ModuleSlashAction extends MornyInternalModule {
	
	override val id: String = "morny.slash"
	override val name: String = "Morny SlashBot Support"
	override val description: String | Null =
		// language=markdown
		"""Reply calls like "抱", "摸摸".
		  |
		  |This module requires *Group Privacy* set to `disabled` on the bot account.
		  |
		  |(@RongSlashBot)[https://t.me/RongSlashBot]
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		eventManager register OnUserSlashAction()
	}
	
}
