package cc.sukazyo.cono.morny

object ServerModulesLoader {
	
	def load (): List[MornyModule] = {
		
		List(
			
			tele_utils.ModuleTeleUtils(),
			randomize_somthing.ModuleRandomize(),
			slash_action.ModuleSlashAction(),
			nbnhhsh.ModuleNbnhhsh(),
			ip186.ModuleIP186(),
			encrypt_tool.ModuleEncryptor(),
			call_me.ModuleCallMe(),
			social_share.ModuleSocialShare(),
			medication_timer.ModuleMedicationTimer(),
			morny_misc.ModuleMornyMisc(),
			uni_meow.ModuleUniMeow(),
			reporter.Module()
			
		)
		
	}
	
}
