package cc.sukazyo.cono.morny.stickers_get

import java.nio.charset.StandardCharsets

enum StickerType {
	case WEBP
	case WEBM
	case PNG
}

object StickerType {
	
	class UnknownStickerTypeException (message: String)
		extends Exception (s"Unknown sticker type: $message")
	
//	@throws[UnknownStickerTypeException]
	def check (stickerFile: Array[Byte]): StickerType =
		val header = new String(stickerFile.take(50), StandardCharsets.UTF_8)
		if header.contains("WEBP") then WEBP
		else if header.contains("webm") then WEBM
		else if header.contains("PNG") then PNG
		else throw UnknownStickerTypeException("Unable to infer file type.")
	
}
