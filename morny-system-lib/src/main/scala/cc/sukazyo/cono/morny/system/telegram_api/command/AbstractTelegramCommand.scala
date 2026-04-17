package cc.sukazyo.cono.morny.system.telegram_api.command

trait AbstractTelegramCommand {
	
	def telegramInfos: List[TCommandInfo]
	
}
