package cc.sukazyo.cono.morny.util

/** Added the [[toHex]] method to [[Byte]] and [[Array]]`[`[[Byte]]`]`.
  *
  * the [[toHex]] method will takes [[Byte]] as a binary byte and convert
  * it to the hex [[String]] that can describe the binary byte. there are
  * always 2 digits unsigned hex number.
  *
  * for example, byte `0` is binary `0000 0000`, it will be converted to
  * `"00"`, and the byte `-1` is binary `1111 1111` which corresponding
  * `"ff"`.
  * {{{
  *     scala> 0.toByte.toHex
  *     val res6: String = 00
  *
  *     scala> 15.toByte.toHex
  *     val res10: String = 0f
  *
  *     scala> -1.toByte.toHex
  *     val res7: String = ff
  * }}}
  *
  * while converting byte array, the order is: the 1st element of the array
  * will be put most forward, then the following added to the tail of hex string.
  * {{{
  *     scala> Array[Byte](0, 1, 2, 3).toHex
  *     val res5: String = 00010203
  * }}}
  *
  */
object ConvertByteHex {
	
	extension (b: Byte) {
		
		/** convert the binary of the [[Byte]] contains to hex string.
		  * @see [[ConvertByteHex]]
		  */
		def toHex: String = (b >> 4 & 0xf).toHexString + (b & 0xf).toHexString
		
	}
	
	extension (data: Array[Byte]) {
		
		/** convert the binary of the [[Array]]`[`[[Byte]]`]` contains to hex string.
		  *
		  * @see [[ConvertByteHex]]
		  */
		def toHex: String =
			val sb = StringBuilder()
			for (b <- data) sb ++= (b toHex)
			sb toString
		
	}
	
}
