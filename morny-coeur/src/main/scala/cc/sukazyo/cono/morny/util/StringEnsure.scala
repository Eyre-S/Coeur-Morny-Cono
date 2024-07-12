package cc.sukazyo.cono.morny.util

object StringEnsure {
	
	extension (str: String) {
		
		/** Take the first line of this String.
		  *
		  * It will find the first `\n` (unicode 0x0a, aka. LF) or `\n` (unicode 0x0e, aka. CR)
		  * and take all the chars before it, and ignores all the contents after it.
		  */
		def firstLine : String =
			str.takeWhile(c => (c != '\n') && (c != '\r'))
		
		/** Ensure the string have a length not smaller that the given length.
		  * 
		  * If the length of the string is smaller than the given length, then the string will be padded
		  * with the given padding character to the right, until the length of the string is not smaller
		  * than the given length.
		  * 
		  * For now, it does nothing if the length of the string is not smaller than the given length.
		  * 
		  * @since 1.1.0
		  * 
		  * @param size The minimum length that the string should have.
		  * @param paddingStr The character that will be used to pad the string. Defaults to `' '` (a space).
		  * @return A string that have been ensured to have a length not smaller than the given length.
		  */
		infix def ensureSize(size: Int, paddingStr: Char = ' '): String = {
			if (str.length < size) {
				val padding = paddingStr.toString * (size-str.length)
				str + padding
			} else str
		}
		
		/** Replace the String with a given char, keeps the String length and characters only at the start
		  * and end.
		  *
		  * This method can be used to de-sensitive some sensitive information, such as phone number, email,
		  * etc. The default setting is to keep the first 2 characters and the last 4 characters, and replace
		  * the rest with '*'.
		  *
		  * Notice that this method have un-defined behavior when the length of the String is less than
		  * the character that will be kept, so change the character length that will be kept in your need.
		  *
		  * 
		  * @example {{{
		  *     scala> val someUserToken = "TOKEN_UV:V927c092FV$REFV[p':V<IE#*&@()U8eR)c"
		  *     val someUserToken: String = TOKEN_UV:V927c092FV$REFV[p':V<IE#*&@()U8eR)c
		  *
		  *     scala> someUserToken.deSensitive()
		  *     val res1: String = TO**************************************eR)c
		  *
		  *     scala> someUserToken.deSensitive(8, 4)
		  *     val res2: String = TOKEN_UV********************************eR)c
		  *
		  *     scala> someUserToken.deSensitive(10, 4, '?')
		  *     val res3: String = TOKEN_UV:V??????????????????????????????eR)c
		  *
		  *     scala> someUserToken.deSensitive(10, 4, '-')
		  *     val res4: String = TOKEN_UV:V------------------------------eR)c
		  *
		  *     scala> "short".deSensitive(5, 5, '-')
		  *     val res5: String = shortshort
		  * }}}
		  * 
		  * @since 1.2.0
		  *
		  * @param keepStart The characters length that need to be kept at the start of the String. Defaults
		  *                  to `2`.
		  * @param keepEnd The characters length that need to be kept at the end of the String. Defaults to
		  *                `4`.
		  * @param sensitive_cover The character that will be used to cover the sensitive information.
		  *                        Defaults to `*`.
		  * @return A string that have been de-sensitive.
		  */
		def deSensitive (keepStart: Int = 2, keepEnd: Int = 4, sensitive_cover: Char = '*'): String =
			(str take keepStart) + (sensitive_cover.toString*(str.length-keepStart-keepEnd)) + (str takeRight keepEnd)
		
	}
	
}
