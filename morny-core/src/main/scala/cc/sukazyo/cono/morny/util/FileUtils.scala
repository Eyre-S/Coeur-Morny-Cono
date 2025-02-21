package cc.sukazyo.cono.morny.util

import java.io.{FileInputStream, IOException}
import java.security.{MessageDigest, NoSuchAlgorithmException}
import scala.util.Using

/**
  * @todo docs
  * @todo some tests?      
  */
object FileUtils {
	
	@throws[IOException|NoSuchAlgorithmException]
	def getMD5Three (path: String): String = {
		val buffer = Array.ofDim[Byte](8192)
		var len = 0
		val algo = MessageDigest.getInstance("MD5")
		Using (FileInputStream(path)) { stream =>
			len = stream.read(buffer)
			while (len != -1)
				algo.update(buffer, 0, len)
				len = stream.read(buffer)
		}
		import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex
		algo.digest toHex
	}
	
}
