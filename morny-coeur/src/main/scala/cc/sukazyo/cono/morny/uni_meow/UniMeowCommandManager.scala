package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.bot.api.SimpleCommandManager

class UniMeowCommandManager extends SimpleCommandManager{
	
	protected[uni_meow] def getCommands: this.commands.type = this.commands
	
}
