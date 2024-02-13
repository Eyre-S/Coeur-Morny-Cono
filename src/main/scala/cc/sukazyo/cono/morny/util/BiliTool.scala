package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.util.UseMath.**

import scala.collection.mutable

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
  * the AV id is a number; the BV id is a special 10 digits base58 number, it shows as String
  * in programming.
  *
  * e.g. while the link ''`https://www.bilibili.com/video/BV17x411w7KC/`'' shows
  * the same with ''`https://www.bilibili.com/video/av170001/`'', the AV id
  * is __`170001`__, the BV id is __`BV17x411w7KC`__.
  *
  * @define AvBvSeeAlso [[https://www.zhihu.com/question/381784377/answer/1099438784 mcfx的回复: 如何看待 2020 年 3 月 23 日哔哩哔哩将稿件的「av 号」变更为「BV 号」？]]
  * @todo Maybe make a class `AV`/`BV` and implement the parse in the class
  */
object BiliTool {
	
	private val V_CONV_XOR = 177451812L
	private val V_CONV_ADD = 8728348608L
	
	private val X_AV_MAX = Math.pow(2, 30).toLong
	private val X_AV_ALT = Int.MaxValue.toLong + 1
	
	private val BB58_TABLE_REV: Map[Char, Int] = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF".toCharArray.zipWithIndex.toMap
	private val BB58_TABLE: Map[Int, Char] = BB58_TABLE_REV.map((k,v) => (v, k))
	private val BB58_TABLE_SIZE: Long = BB58_TABLE.size
	private val BV_TEMPLATE = "1  4 1 7  "
	private val BV_TEMPLATE_FILTER = Array(9, 8, 1, 6, 2, 4)
	
	/** Error of illegal BV id.
	  *
	  * @constructor Build a error with illegal BV details.
	  * @param bv the source illegal BV id.
	  * @param reason why it is illegal.
	  */
	class IllegalFormatException private (bv: String, reason: String)
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
	}
	
	/** Convert an AV video id format to BV video id format for $Bilibili
	  *
	  * $AvBvFormat
	  *
	  * this method '''available while the __av-id < 2^27^__''', while it theoretically
	  * available when the av-id < 2^30^. Meanwhile some digits of the BV id is a fixed
	  * value (like the [[BV_TEMPLATE]] shows) -- input __bv__ can do not follow the format,
	  * but it will almost certainly gives a wrong AV id (because the fixed number is not
	  * processed at all!)
	  *
	  * @see $AvBvSeeAlso
	  *
	  * @param bv a BV id, which should be exactly 10 digits and all chars should be
	  *           a legal base58 char (which means can be found in [[BB58_TABLE]]).
	  *           otherwise, an [[IllegalFormatException]] will be thrown.
	  * @return an AV id which will shows the save video of input __bv__ in $Bilibili
	  * @throws IllegalFormatException when the input __bv__ is not a legal 10 digits base58
	  *                                formatted BV id.
	  */
	@throws[IllegalFormatException]
	def toAv (bv: String): Long = {
		var av = 0L
		if (bv.length != 10) throw IllegalFormatException(bv, bv.length)
		for (i <- BV_TEMPLATE_FILTER.indices) {
			val _get = BV_TEMPLATE_FILTER(i)
			val tableToken = BB58_TABLE_REV get bv(_get)
			if tableToken isEmpty then throw IllegalFormatException(bv, bv(_get), _get)
			av = av + (tableToken.get.toLong * (BB58_TABLE_SIZE**i).toLong)
		}
		av = (av - V_CONV_ADD) ^ V_CONV_XOR
		if (av < 0)
			av+ X_AV_ALT
		else av
	}
	
	/** Convert an AV video format to a BV video format for $Bilibili.
	  *
	  * this method '''available while the __av-id < 2^27^__''', while it theoretically
	  * available when the av-id < 2^30^.
	  *
	  * @param av an AV id.
	  * @return a BV id which will shows the save video of input __av__ in $Bilibili
	  */
	def toBv (av: Long): String = {
		val __av =if (av > X_AV_MAX) av - X_AV_ALT else av
		val _av = (__av^V_CONV_XOR)+V_CONV_ADD
		val bv = Array(BV_TEMPLATE:_*)
		for (i <- BV_TEMPLATE_FILTER.indices) {
			bv(BV_TEMPLATE_FILTER(i)) = BB58_TABLE( (_av/(BB58_TABLE_SIZE**i) % BB58_TABLE_SIZE) toInt )
		}
		String copyValueOf bv
	}
	
}
