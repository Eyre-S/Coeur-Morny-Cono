package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.util.UseMath.**

/** Utils about $Bilibili
  *
  * contains utils:
  *   - av/BV converting:
  *     - [[toAv]]
  *     - [[toBv]]
  *
  * @define Bilibili [[https://bilibili.com Bilibili]]
  *
  * @define AvBvFormat
  * === About AV/BV id format ===
  * the AV id is a int64 number, the max value is 2<sup>51</sup>, and should not be smaller that `1`; the BV
  * id is a special 10 digits base58 number with the first character is constant `1`, it shows as String in
  * programming.
  *
  * e.g. while the link ''`https://www.bilibili.com/video/BV17x411w7KC/`'' shows
  * the same with ''`https://www.bilibili.com/video/av170001/`'', the AV id
  * is __`170001`__, the BV id is __`BV17x411w7KC`__.
  *
  * These algorithms accept and return a AV id as a [[Long]] number, and BV id as a 10 digits base58 [[String]]
  * without the `BV` prefix. For example, the `BV17x411w7KC` will be `"17x411w7KC"` [[String]] in this format, and
  * the `av170001` should be `170001L` [[Long]] val.
  *
  * @define AvBvSeeAlso
  * [bvid说明 - 哔哩哔哩-API收集整理](https://socialsisteryi.github.io/bilibili-API-collect/docs/misc/bvid_desc.html)
  * [旧版本：mcfx 的回答...](https://www.zhihu.com/question/381784377/answer/1099438784)
  *
  */
object BiliTool {
	
	private val V_CONV_XOR: Long = 23442827791579L
	
	private val X_AV_MAX = 1L << 51
	private val X_AV_MASK: Long = X_AV_MAX - 1
	
	private val BB58_TABLE_REV: Map[Char, Int] = "FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf".toCharArray.zipWithIndex.toMap
	private val BB58_TABLE: Map[Int, Char] = BB58_TABLE_REV.map((k,v) => (v, k))
	private val BB58_BASE: Long = BB58_TABLE.size
	private val BV_TEMPLATE: String = "1---------"
	private val BV_TEMPLATE_FILTER: Array[Int] = Array(9, 8, 1, 6, 2, 4, 3, 5, 7)
	
	/** Error of illegal BV id.
	  *
	  * @constructor Build a error with illegal BV details.
	  * @param bv the source illegal BV id.
	  * @param reason why it is illegal.
	  */
	class IllegalBVFormatException private (bv: String, reason: String)
			extends RuntimeException (s"`$bv is not a valid 10 digits base58 BV id: $reason`") {
		
		/** Error of illegal BV id, where the reason is the BV id is not 10 digits.
		  *
		  * @param bv the source of illegal BV id.
		  * @param length the length of the illegal BV id.
		  */
		def this (bv: String, length: Int) =
			this(bv, s"given length is $length")
		
		/** Error of illegal BV id, where the reason is the BV id contains non [[BB58_TABLE base58 character]].
		  *
		  * @param bv the source of illegal BV id.
		  * @param c the illegal character
		  * @param location the index of the illegal character in the illegal BV id.
		  */
		def this (bv: String, c: Char, location: Int) =
			this(bv, s"char `$c` is not in base58 char table (in position $location)")
		
		/** Error of illegal BV id, where the reason is the BV id is not started with `1`.
		  *
		  * @param bv the source of illegal BV id.
		  */
		def this (bv: String) =
			this(bv, s"given BV id $bv is not started with 1 which is required in current version.")
		
	}
	
	
	/** Error of illegal AV id.
	  *
	  * @constructor Build a error with illegal AV details.
	  * @param av     the source illegal AV id.
	  * @param reason why it is illegal.
	  */
	class IllegalAVFormatException private (av: Long, reason: String)
		extends RuntimeException(s"`$av is not a valid AV id: $reason`")
	object IllegalAVFormatException:
		/** Error of illegal AV id, where the reason is the AV id is too large. */
		def thusTooLarge (av: Long) =
			new IllegalAVFormatException(av, s"Given AV id $av is too large, should not be larger than 2^51($X_AV_MAX)")
		/** Error of illegal AV id, where the reason is the AV id is too small. */
		def thusTooSmall (av: Long) =
			new IllegalAVFormatException(av, s"Given AV id $av is too small, should not be smaller than 1")
	
	/** Convert an AV video id format to BV video id format for $Bilibili
	  *
	  * $AvBvFormat
	  *
	  * @see $AvBvSeeAlso
	  *
	  * @param bv a BV id, which should be exactly 10 digits and all chars should be
	  *           a legal base58 char (which means can be found in [[BB58_TABLE]]) and
	  *           the first character should must be 1. BV id in this format does NOT
	  *           contains `BV` prefix. Otherwise, an [[IllegalBVFormatException]]
	  *           will be thrown.
	  * @return an AV id which will shows the save video of input __bv__ in $Bilibili
	  * @throws IllegalBVFormatException when the input __bv__ is not a legal 10 digits base58
	  *                                  formatted BV id.
	  */
	@throws[IllegalBVFormatException]
	def toAv (bv: String): Long = {
		if (bv.length != 10) throw IllegalBVFormatException(bv, bv.length)
		if (bv(0) != '1') throw IllegalBVFormatException(bv)
		val _bv = bv.toCharArray
		val av =
			( for (i <- BV_TEMPLATE_FILTER.indices) yield {
				val _get = BV_TEMPLATE_FILTER(i)
				val tableToken = BB58_TABLE_REV get _bv(_get)
				if tableToken isEmpty then throw IllegalBVFormatException(bv, _bv(_get), _get)
				tableToken.get * (BB58_BASE**i)
			} ).sum
		(av & X_AV_MASK) ^ V_CONV_XOR
	}
	
	/** Convert an AV video format to a BV video format for $Bilibili.
	  *
	  * $AvBvFormat
	  *
	  * @see $AvBvSeeAlso
	  *
	  * @param av an AV id.
	  * @return a BV id which will shows the save video of input __av__ in $Bilibili. A 10 digits
	  *         base58 formatted BV id, does NOT contains `BV` prefix.
	  */
	def toBv (av: Long): String = {
		if (av > X_AV_MAX) throw IllegalAVFormatException.thusTooLarge(av)
		if (av < 1) throw IllegalAVFormatException.thusTooSmall(av)
		var _av = (X_AV_MAX | av) ^ V_CONV_XOR
		val bv = Array(BV_TEMPLATE:_*)
		for (i <- BV_TEMPLATE_FILTER.indices) {
			bv(BV_TEMPLATE_FILTER(i)) = BB58_TABLE((_av % BB58_BASE).toInt)
			_av /= BB58_BASE
		}
		String copyValueOf bv
	}
	
}
