package cc.sukazyo.cono.morny.util

object StringEnsure {
	
	
	extension (str: String) {
		
		def ensureSize(size: Int, paddingStr: Char = ' '): String = {
			if (str.length < size) {
				val padding = paddingStr.toString * (size-str.length)
				str + padding
			} else str
		}
		
		def ensureNotExceed (size: Int, ellipsis: String = "..."): String = {
			if (str.length <= size) str
			else str.take(size) + ellipsis
		}
		
		def deSensitive (keepStart: Int = 2, keepEnd: Int = 4, sensitive_cover: Char = '*'): String =
			(str take keepStart) + (sensitive_cover.toString*(str.length-keepStart-keepEnd)) + (str takeRight keepEnd)
		
	}
	
}
