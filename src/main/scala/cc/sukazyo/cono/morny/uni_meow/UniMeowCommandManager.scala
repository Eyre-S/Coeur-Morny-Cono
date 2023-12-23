package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.bot.command.ISimpleCommand
import cc.sukazyo.cono.morny.bot.command.MornyCommandManager.CommandMap

import scala.collection.mutable

class UniMeowCommandManager {
	
	private[uni_meow] val commands: CommandMap = mutable.SeqMap.empty
	def register [T <: ISimpleCommand] (commands: T*): Unit =
		for (i <- commands)
			this.commands += (i.name -> i)
			for (alias <- i.aliases)
				this.commands += (alias.name -> i)
	
}