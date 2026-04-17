package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv

trait AbstractCommand {
	
	def isMatch (inputCommand: TInputCommand): Boolean
	
	def execute (input: TInputCommand)(using raw: EventEnv): Unit
	
}
