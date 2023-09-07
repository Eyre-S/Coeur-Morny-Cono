package cc.sukazyo.cono.morny.bot.command

trait ITelegramCommand extends ISimpleCommand {
	
	val paramRule: String
	val description: String
	
}
