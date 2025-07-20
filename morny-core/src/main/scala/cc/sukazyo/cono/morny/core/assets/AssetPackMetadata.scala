package cc.sukazyo.cono.morny.core.assets

import io.circe.{Codec, DecodingFailure, ParsingFailure}
import io.circe.generic.semiauto.deriveCodec

case class AssetPackMetadata (
	
	id: String
	
)

object AssetPackMetadata {
	
	implicit val codec_AssetPackMetadata: Codec[AssetPackMetadata] = deriveCodec
	
	@throws[DecodingFailure]
	@throws[ParsingFailure]
	def fromJsonText (jsonText: String): AssetPackMetadata = {
		val json = io.circe.parser.parse(jsonText).toTry.get
		json.as[AssetPackMetadata].toTry.get
	}
	
}
