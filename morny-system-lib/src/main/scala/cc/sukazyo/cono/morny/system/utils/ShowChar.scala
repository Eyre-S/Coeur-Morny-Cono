package cc.sukazyo.cono.morny.system.utils

object ShowChar {
	
	implicit class ShowCharOps (c: Char) {
		
		def show: String = {
			if (!c.isControl) {
				String.format("\\u%04x", c.toInt)
			} else {
				s"'$c'"
			}
		}
		
	}
	
}
