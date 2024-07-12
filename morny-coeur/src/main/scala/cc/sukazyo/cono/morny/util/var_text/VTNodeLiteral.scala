package cc.sukazyo.cono.morny.util.var_text

case class VTNodeLiteral (
	text: String
) extends VTNode {
	
	override def render (vars: Map[String, String]): String =
		text
	
	override def toString: String =
		val prefix           = "literal|"
		val prefix_following = "       |"
		val pt = text.split('\n')
		(pt.headOption.map(prefix + _).getOrElse("") ::
			pt.drop(1).map(prefix_following + _).toList)
			.mkString("\n")
	
	override def serialize: String =
		text
			.replaceAll("/\\{", "//\\{")
			.replaceAll("\\{", "/\\{")
	
}
