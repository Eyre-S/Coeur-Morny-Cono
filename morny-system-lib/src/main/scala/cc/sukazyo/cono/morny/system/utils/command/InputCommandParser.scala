package cc.sukazyo.cono.morny.system.utils.command

import cc.sukazyo.cono.morny.system.utils.ShowChar.ShowCharOps

import scala.collection.mutable.ArrayBuffer
import scala.util.boundary
import scala.util.boundary.break

object InputCommandParser {
	
	object ClassicStrictParser extends InputCommandParser {
//		override val useQuotes: Boolean = true
//		override val strictQuoteFullArgument: Boolean = false
		override val allowQuoteInQuotes: Boolean = false
		override val allowUnclosedQuotes: Boolean = false
		override val allowNewLine: Boolean = false
		override val newLineAsSeparator: Boolean = false
		override val allowNewLineInQuotes: Boolean = false
		override val newLineBreakQuotes: Boolean = false
		override val tabAsSeparator: Boolean = false
		override val allowEscapeAtEnd: Boolean = false
		override val escapeNonSpecialChars: Boolean = false
	}
	
	object ClassicLossyParser extends InputCommandParser {
//		override val useQuotes: Boolean = true
//		override val strictQuoteFullArgument: Boolean = false
		override val allowQuoteInQuotes: Boolean = true
		override val allowUnclosedQuotes: Boolean = true
		override val allowNewLine: Boolean = true
		override val newLineAsSeparator: Boolean = false
		override val allowNewLineInQuotes: Boolean = true
		override val newLineBreakQuotes: Boolean = false
		override val tabAsSeparator: Boolean = false
		override val allowEscapeAtEnd: Boolean = true
		override val escapeNonSpecialChars: Boolean = false
	}
	
	object Default extends InputCommandParser {
//		override val useQuotes: Boolean = true
//		override val strictQuoteFullArgument: Boolean = false
		override val allowQuoteInQuotes: Boolean = true
		override val allowUnclosedQuotes: Boolean = true
		override val allowNewLine: Boolean = true
		override val newLineAsSeparator: Boolean = true
		override val allowNewLineInQuotes: Boolean = true
		override val newLineBreakQuotes: Boolean = true
		override val tabAsSeparator: Boolean = true
		override val allowEscapeAtEnd: Boolean = true
		override val escapeNonSpecialChars: Boolean = false
	}
	
}

trait InputCommandParser {

//	/** Allow quotes to quote a segment of text as one argument, no matter the special chars
//	  * in it. If set false, quotes will be treated as normal text characters. */
//	val useQuotes: Boolean
//	/** A quoted text must be a whole argument, disallow mixes of quoted text and non-quoted
//	  * text in one argument (like `text" that with spaces"~~~`) */
//	val strictQuoteFullArgument: Boolean
	/** Allow the unmatched quotes exists in quoted area (like ' in ""). */
	val allowQuoteInQuotes: Boolean
	/** Allow a quote to be unclosed. If set true, texts from the quote starts till the end of
	  * text will be parsed as one argument. */
	val allowUnclosedQuotes: Boolean
	/** Allow the new line (\n) to be existed outside the quotes. */
	val allowNewLine: Boolean
	/** Make the new line as an argument separator like space char. */
	val newLineAsSeparator: Boolean
	/** Allow a new line in quotes. */
	val allowNewLineInQuotes: Boolean
	/** Makes the new line in quotes immediately end the quote and separate the argument.
	  * If not enabled, the new line will be included in the quoted text. */
	val newLineBreakQuotes: Boolean
	/** Tab character can separate arguments line space char. If disabled, tabs will be treated
	  * as normal text characters. */
	val tabAsSeparator: Boolean
	/** Allow escape character at the most end of input text, it will be treated as normal
	  * characters due to there are nothing can be escaped. */
	val allowEscapeAtEnd: Boolean
	/** Even if the char after the escape char is not special, escape it either. This will make
	  * the escape char disappear where the following char will be output as usual. If disabled,
	  * The escape char will output as-is when the following is non-special character. */
	val escapeNonSpecialChars: Boolean
	
	private object checker {
		inline def isSeparator (c: Char): Boolean =
			if c == ' ' then true
			else if newLineAsSeparator && (c == '\n') then true
			else if tabAsSeparator && (c == '\t') then true
			else false
		inline def isQuote (c: Char): Boolean =
			(c == '\'') || (c == '"')
		inline def isEscape (c: Char): Boolean =
			c == '\\'
		inline def isEscapableInQuote (c: Char): Boolean =
			isQuote(c) || isEscape(c)
		inline def isEscapable (c: Char): Boolean =
			isEscapableInQuote(c) || isSeparator(c)
		inline def isForbidden (c: Char): Boolean =
			if !allowNewLine && ((c == '\n') || (c == '\r')) then true
			else false
		inline def isForbiddenInQuotes (c: Char): Boolean =
			if !allowNewLineInQuotes && ((c == '\n') || (c == '\r')) then true
			else false
		inline def isIgnore (c: Char): Boolean =
			if allowNewLine && (c == '\r') then true
			else false
		inline def isIgnoreInQuotes (c: Char): Boolean =
			if allowNewLineInQuotes && (c == '\r') then true
			else false
	}
	
	def parse (input: String): (args: Array[String], remainsRaw: String) = {
		
		import checker.*
		
		val parsed = ArrayBuffer[String]()
		val remains = StringBuilder()
		
		var i = 0
		var curr = StringBuilder()
		var inQuote: Null|Char = null
		var inEscaped: Null|Char = null
		inline def c = input(i)
		inline def hasNext = i+1 < input.length
		inline def isInQuotes = inQuote != null
		inline def continue()(using label: boundary.Label[Unit]): Unit = { i += 1; break() }
		while (i < input.length) { boundary {
			
			// now first arg is already parsed, put everything remains to remains
			if (parsed.nonEmpty) {
				remains += c
			}
			
			if (isInQuotes) {
				if (isForbiddenInQuotes(c))
					throw IllegalArgumentException(
						s"""Not allowed characters in a quoted text: ${c.show}
						   |  At :$i under:
						   |${input.indent(4)}""".stripMargin
					)
				if (isIgnoreInQuotes(c))
					continue()
			}
			else {
				if (isForbidden(c))
					throw IllegalArgumentException(
						s"""Not allowed characters in input: ${c.show}
						   |  At :$i under:
						   |${input.indent(4)}""".stripMargin
					)
				if (isIgnore(c))
					continue()
			}
			
			if (inEscaped != null) {
				if (if isInQuotes then isEscapableInQuote(c) else isEscapable(c))
					curr += c
				else if escapeNonSpecialChars then
					curr += c
				else
					curr += inEscaped.asInstanceOf[Char] += c
				inEscaped = null
				continue()
			}
			if (isEscape(c)) {
				inEscaped = '\\'
				continue()
			}
			
			if (isQuote(c)) {
				if !isInQuotes then
					inQuote = c
				else if inQuote != c then
					if allowQuoteInQuotes then
						curr += c
					else
						throw IllegalArgumentException(
							s"""Mismatched quote character in a quoted text: expected $inQuote but got $c
							   |  At :$i under:
							   |${input.indent(4)}""".stripMargin
						)
				else
					inQuote = null
				continue()
			}
			
			if (!isInQuotes && isSeparator(c)) {
				if (curr.nonEmpty)
					parsed += curr.toString
				curr = curr.empty
				continue()
			}
			
			curr += c
			continue()
			
		}}
		
		if (inEscaped != null) {
			if allowEscapeAtEnd then
				curr += inEscaped.asInstanceOf[Char]
			else throw IllegalArgumentException(
				s"""There are no next character to be escaped for an escape character.
				   |  At :$i under:
				   |${input.indent(4)}""".stripMargin
			)
		}
		
		if ((inQuote != null) && !allowUnclosedQuotes) {
			throw IllegalArgumentException(
				s"""Reached end of input while still in a quoted text started with $inQuote
				   |  At :$i under:
				   |${input.indent(4)}""".stripMargin
			)
		}
		
		if (curr.nonEmpty)
			parsed += curr.toString
		
		(parsed.toArray, remains.toString)
		
	}
	
}
