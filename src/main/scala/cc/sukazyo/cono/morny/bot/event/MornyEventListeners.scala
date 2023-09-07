package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListenerManager

object MornyEventListeners {
	
	def registerAllEvents(): Unit = {
		
		EventListenerManager.register(
			// ACTIVITY_RECORDER
			OnUpdateTimestampOffsetLock,
			// KUOHUANHUAN_NEED_SLEEP
			OnTelegramCommand,
			OnUniMeowTrigger,
			OnUserRandom,
			OnQuestionMarkReply,
			OnUserSlashAction,
			OnInlineQuery,
			OnCallMe,
			OnCallMsgSend,
			OnMedicationNotifyApply,
			OnEventHackHandle
		)
		
	}
	
}
