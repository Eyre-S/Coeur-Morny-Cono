package cc.sukazyo.cono.morny.util.hytrans

object Parser {
	
	def parse (document: String): Definitions = {
		
		val lines = document.replaceAll("\\r", "").split('\n')
		
		val keyValues = collection.mutable.Map.empty[String, String]
		var keyDef: String | Null = null
		def newValue = StringBuilder()
		var valueDef: StringBuilder = newValue
		def addLine (line: String) =
			valueDef ++= line += '\n'
		//noinspection TypeAnnotation
		def saveThis() =
			if keyDef != null then
				keyValues += (keyDef -> valueDef.toString.stripSuffix("\n"))
				keyDef = null
				valueDef = newValue
		
		lines.foreach { line =>
			line.headOption match {
				case Some(' ') | Some('\t') | None => // empty lines, will be ignored
				case Some('#') => // comment line, will be ignored
				case Some('%') => // document meta definition line, currently not supported
				case Some('&') => // document meta definition line, currently not supported
				case Some('|') => // content line
					addLine(line drop 2)
				case Some(_) => // a key definition line
					saveThis()
					keyDef = line
			}
		}
		
		saveThis()
		
		Definitions(keyValues.toMap)
		
	}
	
}
