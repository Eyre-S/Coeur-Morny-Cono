package cc.sukazyo.cono.morny.util

object StringEnsure {
	
	
	extension (str: String) {
		
		def ensureSize(size: Int, paddingStr: Char = ' '): String = {
			if (str.length < size) {
				val padding = paddingStr.toString * (size-str.length)
				str + padding
			} else str
		}
		
		def deSensitive (keepStart: Int = 2, keepEnd: Int = 4, sensitive_cover: Char = '*'): String =
			(str take keepStart) + (sensitive_cover.toString*(str.length-keepStart-keepEnd)) + (str takeRight keepEnd)
		
	}
	
}
