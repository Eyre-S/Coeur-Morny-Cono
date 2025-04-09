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
	
}
