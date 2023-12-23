package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.MornyCoeur.OnInitializingContext

class MornyCoreModule extends MornyModule {
	
	override val id: String = "cc.sukazyo.cono.morny.bot"
	override val name: String = "Morny Coeur - Core (for refactor temporary)"
	override val version: String = MornySystem.VERSION
	
	override val description: String | Null =
		"""Core module of Morny Coeur.
		  |
		  |Exists for temporary use, when refactor completed it should be replaced
		  |by all other small modules that provide different functionality.
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: OnInitializingContext): Unit = {
		import cc.sukazyo.cono.morny.bot.command.*
		import cc.sukazyo.cono.morny.bot.event.*
		import cc.sukazyo.cono.morny.bot.query.*
		import cxt.*
		
		val $OnUserRandom = OnUserRandom()
		eventManager.register(
			// ACTIVITY_RECORDER
			// KUOHUANHUAN_NEED_SLEEP
			$OnUserRandom.RandomSelect,
			//noinspection NonAsciiCharacters
			$OnUserRandom.尊嘟假嘟,
			OnQuestionMarkReply(),
			OnUserSlashAction(),
			OnCallMe(),
			OnCallMsgSend(),
			OnGetSocial(),
			OnMedicationNotifyApply(),
			OnEventHackHandle()
		)
		
		val $MornyHellos = MornyHellos()
		val $IP186Query = IP186Query()
		val $MornyInformation = MornyInformation()
		val $MornyInformationOlds = MornyInformationOlds(using $MornyInformation)
		val $MornyManagers = MornyManagers()
		//noinspection NonAsciiCharacters
		val $创 = 创()
		commandManager.register(
			
			$MornyHellos.On,
			$MornyHellos.Hello,
			MornyInfoOnStart(),
			GetUsernameAndId(),
			EventHack(),
			Nbnhhsh(),
			$IP186Query.IP,
			$IP186Query.Whois,
			Encryptor(),
			MornyOldJrrp(),
			GetSocial(),
			
			$MornyManagers.SaveData,
			$MornyInformation,
			$MornyInformationOlds.Version,
			$MornyInformationOlds.Runtime,
			$MornyManagers.Exit,
			
			Testing(),
			DirectMsgClear(),
			//noinspection NonAsciiCharacters
			$创.Chuang
			
		)
		
		queryManager.register(
			RawText(),
			MyInformation(),
			ShareToolTwitter(),
			ShareToolBilibili(),
			ShareToolSocialContent()
		)
		
	}
	
}
