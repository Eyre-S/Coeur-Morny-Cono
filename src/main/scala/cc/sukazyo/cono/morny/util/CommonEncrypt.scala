package cc.sukazyo.cono.morny.util

import java.nio.charset.{Charset, StandardCharsets}
import java.security.{MessageDigest, NoSuchAlgorithmException}

/** Provides some re-encapsulated algorithm function, and some standard values in encrypting,
  * and some normalized utils in processing something in encrypting.
  *
  * currently there's:
  *   - standard value:
  *     - [[ENCRYPT_STANDARD_CHARSET]] the standard [[Charset]] to parse between [[String]]
  *       and [[Bin]] in encrypting.
  *   - algorithm encapsulations:
  *     - [[MD5]] (MD5 Message-Digest Algorithm)
  *     - [[SHA1]] (Secure Hash Algorithm 1)
  *     - [[SHA256]] (Secure Hash Algorithm 2: 256bit)
  *     - [[SHA512]] (Secure Hash Algorithm 2: 512bit)
  *   - normalized utils
  *     - [[lint_base64FileName]] remove the .base64 file-extension for base64 text file
  *
  * @define WhenString2Bin
  * [[String]] will encoded to [[Bin]] using [[Charset]] [[ENCRYPT_STANDARD_CHARSET]]
  *
  * @todo some tests
  */
object CommonEncrypt {
	
	/** the [[Charset]] should use when converting between [[String]]
	  * and [[Bin]] in encrypting */
	val ENCRYPT_STANDARD_CHARSET: Charset = StandardCharsets.UTF_8
	
	/** the alias of [[Array]]`[`[[Byte]]`]`.
	  * means the binary data.
	  */
	//noinspection ScalaWeakerAccess
	type Bin = Array[Byte]
	
	private def hash (data: Bin)(using algorithm: String): Bin =
		try {
			MessageDigest.getInstance(algorithm) digest data
		} catch case n: NoSuchAlgorithmException =>
			throw IllegalStateException(n)
	
	/** the [[https://en.wikipedia.org/wiki/MD5 MD5]] hash value of input [[Bin]] `data`. */
	def MD5(data: Bin): Bin = hash(data)(using "md5")
	/** the [[https://en.wikipedia.org/wiki/MD5 MD5]] hash value of input [[String]] `data`.
	  *
	  * $WhenString2Bin
	  */
	def MD5 (data: String): Bin = hash(data getBytes ENCRYPT_STANDARD_CHARSET)(using "md5")
	
	/** the [[https://en.wikipedia.org/wiki/SHA-1 SHA-1]] hash value of input [[Bin]] `data`. */
	def SHA1 (data: Bin): Bin = hash(data)(using "sha1")
	/** the [[https://en.wikipedia.org/wiki/SHA-1 SHA-1]] hash value of input [[String]] `data`.
	  *
	  * $WhenString2Bin
	  */
	def SHA1 (data: String): Bin = hash(data getBytes ENCRYPT_STANDARD_CHARSET)(using "sha1")
	
	/** the [[https://en.wikipedia.org/wiki/SHA-2 SHA-2/256]] hash value of input [[Bin]] `data`. */
	def SHA256 (data: Bin): Bin = hash(data)(using "sha256")
	/** the [[https://en.wikipedia.org/wiki/SHA-2 SHA-2/256]] hash value of input [[String]] `data`.
	  *
	  * $WhenString2Bin
	  */
	def SHA256 (data: String): Bin = hash(data getBytes ENCRYPT_STANDARD_CHARSET)(using "sha256")
	
	/** the [[https://en.wikipedia.org/wiki/SHA-2 SHA-2/512]] hash value of input [[Bin]] `data`. */
	def SHA512 (data: Bin): Bin = hash(data)(using "sha512")
	/** the [[https://en.wikipedia.org/wiki/SHA-2 SHA-2/512]] hash value of input [[String]] `data`.
	  *
	  * $WhenString2Bin
	  */
	def SHA512 (data: String): Bin = hash(data getBytes ENCRYPT_STANDARD_CHARSET)(using "sha512")
	
	/** Try get the filename before it got encrypted.
	  *
	  * It assumes the base64 encrypted file should keep the original file, and plus
	  * a file-extension shows the file is base64 encrypted.
	  *
	  * Actually, the file will try find the following file-extension and drop it:
	  *   - `.b64`
	  *   - `.64.txt`
	  *   - `.base64`
	  *   - `.base64.txt`
	  * if none of those found, it will do no process anymore.
	  *
	  * @param encrypted the file fullname (means filename with file-extension) of base64 encrypted file.
	  * @return the file fullname removed the base64 file extension.
	  */
	def lint_base64FileName (encrypted: String): String = encrypted match
		case i  if i  endsWith ".b64"        => i  dropRight ".b64".length
		case ix if ix endsWith ".b64.txt"    => ix dropRight ".b64.txt".length
		case l  if l  endsWith ".base64"     => l  dropRight ".base64".length
		case lx if lx endsWith ".base64.txt" => lx dropRight ".base64.txt".length
		case u => u
	
	/** Hash a [[Long]] id to [[Bin]] using [[MD5]] algorithm.
	  *
	  * For some privacy cases, this method can provide a standard way to hash a ID to a MD5 hash value.
	  *
	  * @param id The [[Long]] number typed id.
	  * @return The hash value of the id.
	  */
	def hashId (id: Long): Bin =
		MD5(id.toString)
	
}
