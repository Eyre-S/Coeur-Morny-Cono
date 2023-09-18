package cc.sukazyo.cono.morny.util

import scala.collection.mutable.ArrayBuffer
import scala.util.boundary

/**
  * @todo docs
  * @todo maybe there can have some encapsulation
  */
object UniversalCommand {
	
	def apply (input: String): Array[String] = {
		
		val builder = ArrayBuffer.empty[String]
		
		extension (c: Char) {
			private inline def isUnsupported: Boolean =
				(c == '\n') || (c == '\r')
			private inline def isSeparator: Boolean =
				c == ' '
			private inline def isQuote: Boolean =
				(c == '\'') || (c == '"')
			private inline def isEscapeChar: Boolean =
				c == '\\'
			private inline def escapableInQuote: Boolean =
				c.isQuote || c.isEscapeChar
			private inline def escapable: Boolean =
				c.escapableInQuote || c.isSeparator
		}
		
		var arg = StringBuilder()
		var i = 0
		while (i < input.length) {
			if (input(i) isSeparator) {
				if (arg nonEmpty) builder += arg.toString
				arg = arg.empty
			} else if (input(i) isQuote) {
				val _inside_tag = input(i)
				boundary { while (true) {
					i=i+1
					if (i >= input.length) throw IllegalArgumentException("UniversalCommand: unclosed quoted text")
					if (input(i) == _inside_tag)
						boundary.break()
					else if (input(i) isUnsupported)
						throw IllegalArgumentException("UniversalCommand: unsupported new-line")
					else if (input(i) isQuote)
						throw IllegalArgumentException("UniversalCommand: mixed \" and ' used")
					else if (input(i) isEscapeChar)
						if (i+1 >= input.length) throw IllegalArgumentException("UniversalCommand: \\ in the end")
						if (input(i+1) escapableInQuote)
							i=i+1
						arg += input(i)
					else
						arg += input(i)
				}}
			} else if (input(i) isUnsupported) {
				throw IllegalArgumentException("UniversalCommand: unsupported new-line")
			} else if (input(i) isEscapeChar) {
				if (i + 1 >= input.length) throw IllegalArgumentException("UniversalCommand: \\ in the end")
				if (input(i+1) escapable)
					i=i+1
				arg += input(i)
			} else {
				arg += input(i)
			}
			i = i + 1
		}
		if (arg nonEmpty) builder += arg.toString
		
		builder toArray
		
	}
	
}
