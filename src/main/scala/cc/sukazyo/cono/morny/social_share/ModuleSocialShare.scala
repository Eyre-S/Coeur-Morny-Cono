package cc.sukazyo.cono.morny.social_share

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur

class ModuleSocialShare extends MornyInternalModule {
	
	override val id: String = "morny.social"
	override val name: String = "Morny Social Media Share Tools"
	override val description: String | Null =
		"""Provides a serial tools contains refactor share url, get content from
		  |social media, and more.
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		queryManager register query.ShareToolTwitter()
		queryManager register query.ShareToolBilibili()
		
		commandManager register command.GetSocial()
		eventManager register event.OnGetSocial()
		queryManager register query.ShareToolSocialContent()
		
	}
	
}
 