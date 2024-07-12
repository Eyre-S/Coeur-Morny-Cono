package cc.sukazyo.cono.morny.util.hytrans

case class LangTag (
	lang: String,
	priority: Long
) extends Comparable[LangTag] {
	
	override def compareTo(o: LangTag): Int =
		this.priority `compareTo` o.priority
	
}

object LangTag {
	
	class IllegalLangTagException (message: String, val original: String)
		extends IllegalArgumentException(message)
	
	def normalizeLangTag(tag: String): String =
		tag.replaceAll("-", "_").toLowerCase
	
	@throws[IllegalLangTagException]
	def ensureLangTag (tag: String): String =
		tag.foreach {
			case c if c.isLetter =>
			case '_' =>
			case ' ' | '\t' | '\r' | '\n' =>
				throw IllegalLangTagException("Lang Tag cannot contains space", tag)
			case ill =>
				throw IllegalLangTagException(s"Illegal character '$ill' in Lang Tag \"$tag\"", tag)
		}
		tag
	
}
