package cc.sukazyo.cono.morny.util.var_text

import cc.sukazyo.cono.morny.util.var_text.Var.isLegalId

import scala.language.implicitConversions

/** A text/string template that may contains some named replaceable variables. It's concept may
  * be similar with scala's `StringContext` or `GString` in groovy.
  *
  * A [[VarText]] can contains a stream of [[VTNode]]s, each nodes can be a [[VTNodeLiteral]] or
  * a [[VTNodeVar]].
  *
  * This can be rendered to a native [[String]] by calling [[render]] method with a set of [[Var]]
  * variables. The [[VTNodeVar]] will look for the given [[Var]]s to find if there's a match, and
  * replace itself with the value of the [[Var]], or output a placeholder if there's no match.
  * 
  * @since 2.0.0
  */
trait VarText {
	
	val nodes: List[VTNode]
	
	/** Render this VarText with the given `(var-key -> value)` map.
	  * @since 2.0.0 
	  */
	def render (vars: Map[String, String]): String =
		nodes.map(_.render(vars)).mkString
	
	/** Render this VarText with the given [[Var]]s seq.
	  * @since 2.0.0 
	  */
	def render (vars: Var*): String =
		render(Map.from(vars.toList.map(_.unpackKV)))
	
	/** Inspect the nodes of this VarText.
	  * 
	  * Each node will be rendered to a line with the node types prefix.
	  * 
	  * @since 2.0.0
	  */
	override def toString: String =
		nodes.map(_.toString).mkString("\n")
	
	/** Serialize this VarText to a template string.
	  * 
	  * The return template string will be like the original template string that can be parsed
	  * by the [[VarText.apply(String)]] parser.
	  * 
	  * @since 2.0.0 
	  */
	def serialize: String =
		nodes.map(_.serialize).mkString
	
}

object VarText {
	
	def apply (_nodes: VTNode*): VarText = new VarText:
		override val nodes: List[VTNode] = _nodes.toList
	
	implicit def VarText_is_String (varText: VarText): String =
		varText.render()
	
	private val symbol_escape = '/'
	private val symbol_var_start = '{'
	private val symbol_var_end = '}'
	
	/** Parse a serialized VarText template string to a [[VarText]] object.
	  * 
	  * In the current standard, the `{<param>}` will be parsed to a [[VTNodeVar]], unless it
	  * is escaped by the escape char `/`; And the escape char can and can only escape [[VTNodeVar]]
	  * starter `{` or escape char `/` itself, any other chars following the escape char will
	  * be treated both escape char itself and the following char as a normal char; And all others
	  * will be parsed to [[VTNodeLiteral]].
	  * 
	  * @since 2.0.0
	  */
	def apply (template: String): VarText = {
		
		val _nodes = collection.mutable.ListBuffer[VTNode]()
		
		def newBuffer = StringBuilder()
		var buffer: StringBuilder = newBuffer
		def pushc (c: Char): Unit =
			buffer += c
		def buffer2literal(): Unit =
			_nodes += VTNodeLiteral(buffer.toString)
			buffer = newBuffer
		def buffer2var(): Unit =
			_nodes += VTNodeVar(buffer.toString drop 1)
			buffer = newBuffer
		sealed trait State
		case class in_escape(it: Char) extends State
		case object literal extends State
		case object in_var_def extends State
		var state: State = literal
		
		template.foreach { i =>
			
			def push(): Unit =
				buffer += i
			
			state match
				case in_escape(e) =>
					i match
						case f if f == symbol_var_start =>
							state = literal
							push()
						case f if f == symbol_escape =>
							state = literal
							push()
						case _ =>
							state = literal
							pushc(e)
							push()
				case _: in_var_def.type =>
					i match
						case f if f == symbol_var_end =>
							buffer2var()
							state = literal
						case _ if isLegalId(i) =>
							push()
						case _ =>
							state = literal
							push()
				case _: literal.type =>
					i match
						case f if f == symbol_escape =>
							state = in_escape(i)
						case f if f == symbol_var_start =>
							buffer2literal()
							state = in_var_def
							push()
						case _ =>
							push()
			
		}
		
		state match
			case in_escape(e) =>
				pushc(e)
			case _ =>
		buffer2literal()
		
		new VarText:
			override val nodes: List[VTNode] =
				_nodes.toList
					.filterNot {
						case VTNodeLiteral(text) if text.isEmpty =>
							true
						case _ => false
					}
		
	}
	
}
