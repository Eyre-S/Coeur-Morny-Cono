package cc.sukazyo.cono.morny.system.telegram_api.command.name

trait CommandNameSpec {
	
	def name (name: String): CommonName =
		CommonName(name)
	
	def hidden (name: String): HiddenName =
		HiddenName(name)
	
	def secret (token: String): EncryptedName =
		EncryptedName(token)
	
}
