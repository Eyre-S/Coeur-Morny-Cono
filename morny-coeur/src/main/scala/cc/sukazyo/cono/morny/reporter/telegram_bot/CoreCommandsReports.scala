package cc.sukazyo.cono.morny.reporter.telegram_bot

import cc.sukazyo.cono.morny.core.event.TelegramCoreCommandEvents
import cc.sukazyo.cono.morny.reporter.MornyReport

class CoreCommandsReports (using reporter: MornyReport) {
	
	private val _Event = TelegramCoreCommandEvents.inCoeur(using reporter.coeur)
	
	val onUnauthorizedManageCommandCall: _Event.OnUnauthorizedManageCommandCall.MyCallback
	= (context, command) => {
		reporter.unauthenticatedAction(
			s"/${command.name}",
			context.bind_user
		)
	}
	
}
