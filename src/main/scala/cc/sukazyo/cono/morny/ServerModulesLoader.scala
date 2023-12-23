package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.event_hack.ModuleEventHack
import cc.sukazyo.cono.morny.uni_meow.ModuleUniMeow

object ServerModulesLoader {
	
	def load (): List[MornyModule] = {
		
		List(
			ModuleEventHack(),
			ModuleUniMeow(),
			MornyCoreModule()
		)
		
	}
	
}
