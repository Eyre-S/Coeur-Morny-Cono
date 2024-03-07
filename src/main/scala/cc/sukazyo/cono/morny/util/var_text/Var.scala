package cc.sukazyo.cono.morny.util.var_text

import scala.language.implicitConversions

/** A Var is a key-value pair, where the key is a string, and the value is also a string.
  *
  * This class is used to represent a variable in the [[VarText]].
  *
  * You can just call the [[Var]] constructor to create a new Var, or use the implicit
  * conversion to create a var from a tuple of ([[String]], [[String]]), or use the extension
  * method [[Var.StringAsVarText.asVar]] to convert a [[String]] to a var.
  * 
  * @since 2.0.0
  *
  * @param id The key of the variable, also known as var-id.
  *
  *           The var-id have some limitation on the characters that can be used in it.
  *           For details, see [[Var.isLegalId]]. An illegal id will cause the constructor
  *           throws [[IllegalArgumentException]].
  *
  * @param text The text content of the variable.
  */
case class Var (
	id: String,
	text: String
) {
	
	// todo: id limitation
	id.foreach { c =>
		if (!Var.isLegalId(c))
			throw new IllegalArgumentException(s"Character $c (${c.toInt}) is not allowed in a var id")
	}
	
	/** Create a new Var with the same text but different id.
	  * @since 2.0.0
	  */
	def asId (id: String): Var =
		Var(id, this.text)
	
	/** Create a new Var with the same id but different text.
	  * @since 2.0.0
	  */
		Var(this.id, text)
	
	/** Unpack this Var into a ([[String]], [[String]]) tuple.
	  * @since 2.0.0
	  */
	def unpackKV: (String, String) =
		(id, text)
	
}

object Var {
	
	private val ID_AVAILABLE_SYMBOLS: Set[Char] =
		"_-.*/\\|:#@%&?;,~"
			.toCharArray.toSet
	
	/** Is this character is a legal var-id character.
	  *
	  * In other words, if this character is a letter, a digit, or one of the following
	  * symbols (`_` `-` `.` `*` `/` `\` `|` `:` `#` `@` `%` `&` `?` `;` `,` `~`), this
	  * character is allowed to shows in the [[Var.id]], we said this character is a
	  * legal var-id character.
	  *
	  * @since 2.0.0
	  * 
	  * @return `true` if this character is legal, false otherwise.
	  */
	def isLegalId (c: Char): Boolean =
		c.isLetterOrDigit || ID_AVAILABLE_SYMBOLS.contains(c)
	
	/** Convert a tuple of ([[String]], [[String]]) to a Var.
	  * 
	  * The first string of the tuple will be the [[Var.id]], and the second string
	  * will be the [[Var.text]]
	  * 
	  * @since 2.0.0
	  */
	implicit def StrStrTupleAsVar (tuple: (String, Any)): Var =
		Var(tuple._1, tuple._2.toString)
	
	/** @see [[asVar]] */
	implicit class StringAsVarText (text: String):
		/** Convert this string text to a [[Var]].
		  * @since 2.0.0
		  * @param id the var-id.
		  * @return a [[Var]] that the [[Var.text text]] is this string, and the [[Var.id id]]
		  *         is the given id.
		  */
		def asVar (id: String): Var =
			Var(id, text)
	
}
