package cc.sukazyo.cono.morny.util.var_text

class VTNodeVar (
	var_id: String
) extends VTNode {
	
	override def render (vars: Map[String, String]): String =
		vars.getOrElse(var_id, s"$${$var_id}")
	
	override def toString: String =
		s"var_def(id={$var_id})"
	
	override def serialize: String =
		s"{$var_id}"
	
}
