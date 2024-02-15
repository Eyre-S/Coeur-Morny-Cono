package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.bot.api.MornyCommandManager.CommandMap

import scala.collection.mutable

trait SimpleCommandManager {
	
	protected val commands: CommandMap = mutable.SeqMap.empty
	
	protected def doRegister [T <: ISimpleCommand](it: T): Unit =
		this.commands += (it.name -> it)
		for (alias <- it.aliases)
			this.commands += (alias.name -> it)
	
	infix def register [T <: ISimpleCommand] (command: T): Unit =
		doRegister(command)
	def register [T <: ISimpleCommand] (commands: T*): Unit =
		for (command <- commands) doRegister(command)
	
}
