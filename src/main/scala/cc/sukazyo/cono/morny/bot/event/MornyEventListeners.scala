package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListenerManager
import cc.sukazyo.cono.morny.MornyCoeur

class MornyEventListeners (using manager: EventListenerManager) (using coeur: MornyCoeur)  {
	
	manager.register(
		// ACTIVITY_RECORDER
		// KUOHUANHUAN_NEED_SLEEP
		OnUniMeowTrigger(using coeur.commands),
		OnUserRandom(),
		OnQuestionMarkReply(),
		OnUserSlashAction(),
		OnCallMe(),
		OnCallMsgSend(),
		OnMedicationNotifyApply(),
		OnEventHackHandle()
	)
	
}
