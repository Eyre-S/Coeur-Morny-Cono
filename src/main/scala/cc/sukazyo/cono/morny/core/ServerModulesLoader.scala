package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny

object ServerModulesLoader {
	
	def load (): List[MornyModule] = {
		
		List(
			
			morny.tele_utils.ModuleTeleUtils(),
			morny.randomize_somthing.ModuleRandomize(),
			morny.slash_action.ModuleSlashAction(),
			morny.nbnhhsh.ModuleNbnhhsh(),
			morny.ip186.ModuleIP186(),
			morny.encrypt_tool.ModuleEncryptor(),
			morny.call_me.ModuleCallMe(),
			morny.social_share.ModuleSocialShare(),
			morny.medication_timer.ModuleMedicationTimer(),
			morny.morny_misc.ModuleMornyMisc(),
			morny.uni_meow.ModuleUniMeow(),
			morny.reporter.Module()
			
		)
		
	}
	
}
