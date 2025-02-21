package cc.sukazyo.cono.morny.util.var_text

trait VTNode {
	
	def render (vars: Map[String, String]): String
	
	def serialize: String
	
}
