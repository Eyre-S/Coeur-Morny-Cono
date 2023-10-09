package cc.sukazyo.cono.morny.util

object StringEnsure {
	
	
	extension (str: String) {
		
		def ensureSize(size: Int, paddingStr: Char = ' '): String = {
			if (str.length < size) {
				val padding = paddingStr.toString * (size-str.length)
				str + padding
			} else str
		}
		
	}
	
}
