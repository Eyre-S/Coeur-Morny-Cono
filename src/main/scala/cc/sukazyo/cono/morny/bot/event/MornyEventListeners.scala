package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListenerManager
import cc.sukazyo.cono.morny.MornyCoeur

class MornyEventListeners (using manager: EventListenerManager) (using coeur: MornyCoeur)  {
	
	private val $OnUserRandom = OnUserRandom()
	manager.register(
		// ACTIVITY_RECORDER
		// KUOHUANHUAN_NEED_SLEEP
		OnOnAlias(),
		OnUniMeowTrigger(using coeur.commands),
		OnQuestionMarkReply(),
		$OnUserRandom.RandomSelect,
		//noinspection NonAsciiCharacters
		$OnUserRandom.尊嘟假嘟,
		OnUserSlashAction(),
		OnCallMe(),
		OnCallMsgSend(),
		OnGetSocial(),
		OnMedicationNotifyApply(),
		OnEventHackHandle()
	)
	
}
