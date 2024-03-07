package cc.sukazyo.cono.morny.util

object UseString {
	
	/** Convert a list of [[String]] to a multiline string.
	  *
	  * Each input string will be a line in the new string, and the the string
	  * that equals to `null` will be ignored.
	  * 
	  * @example {{{
	  * scala> println(MString(
	  *      "line1", // line 1
	  *      "line2", // line 2
	  *      "",      // line 3
	  *      null,    // will be ignored
	  *      "line4", // line 4
	  *  ))
	  * line1
	  * line2
	  *
	  * line4
	  * }}}
	  * 
	  * @since 2.0.0
	  */
	def MString (lines: String*): String =
		lines.filterNot(_ == null).mkString("\n")
	
	/** A simple string interpolator implementation that fixed [[scala.collection.immutable.StringOps.stripMargin]]
	  * will remove the interpolated string's `|` when using s"" or f"" at the
	  * same time.
	  *
	  * @see [[m]]
	  */
	implicit class MString (private val sc: StringContext) extends AnyVal:
		/** A simple string interpolator implementation that fixed [[scala.collection.immutable.StringOps.stripMargin]]
		  * will remove the interpolated string's `|` when using s"" or f"" at
		  * the same time.
		  *
		  * It will process stripMargin for each raw texts, then do the s""
		  * interpolation. So, the interpolated string's margin character (`|`)
		  * will be kept.
		  *
		  * This should be useful when inserting a ascii table or ascii art when
		  * using string interpolator.
		  *
		  * @example {{{
		  * scala> val table =
		  *      raw""" +--------+
		  *           | |  head  |
		  *           | +--------+
		  *           | |  body  |
		  *           | |  next  |
		  *           | +--------+
		  *           |""".stripMargin
		  *
		  * val table: String = " +--------+
		  *  |  head  |
		  *  +--------+
		  *  |  body  |
		  *  |  next  |
		  *  +--------+
		  * "
		  *
		  * scala> println(table)
		  *  +--------+
		  *  |  head  |
		  *  +--------+
		  *  |  body  |
		  *  |  next  |
		  *  +--------+
		  *
		  *
		  * scala> println(
		  *      s"""Here is a table:
		  *         |$table
		  *         |""".stripMargin)
		  * Here is a table:
		  *  +--------+
		  *   head  |
		  *  +--------+
		  *   body  |
		  *   next  |
		  *  +--------+
		  *
		  *
		  *
		  * scala> println(
		  *      m"""Here is a table:
		  *         |$table
		  *         |""")
		  * Here is a table:
		  *  +--------+
		  *  |  head  |
		  *  +--------+
		  *  |  body  |
		  *  |  next  |
		  *  +--------+
		  * }}}
		  *
		  * @since 2.0.0
		  */
		def m (args: Any*): String = {
			StringContext(sc.parts.map(_.stripMargin)*)
				.s(args*)
		}
	
}
