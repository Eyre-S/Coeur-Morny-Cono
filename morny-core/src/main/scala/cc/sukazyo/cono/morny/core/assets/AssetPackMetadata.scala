package cc.sukazyo.cono.morny.core.assets

import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, DecodingFailure, ParsingFailure}

/** Assets pack metadata, also known as `morny-assets.jsonc` under assets pack.
  * 
  * @param id Unique identifier of this assets pack. This must be unique among all assets packs.
  */
case class AssetPackMetadata (
	
	id: String
	
)

/** Utils for [[AssetPackMetadata! assets pack metadata]].
 * 
 * @see [[AssetPackMetadata!]]
  */
object AssetPackMetadata {
	
	implicit val codec_AssetPackMetadata: Codec[AssetPackMetadata] = deriveCodec
	
	/** Parse an [[AssetPackMetadata!]] from JSON text.
	  * 
	  * @throws DecodingFailure If the JSON text is not a valid JSON.
	  * @throws ParsingFailure If the JSON text cannot be decoded into an [[AssetPackMetadata!]]
	  *                        (e.g. not satisfy the schema of [[AssetPackMetadata!]]).
	  */
	@throws[DecodingFailure]
	@throws[ParsingFailure]
	def fromJsonText (jsonText: String): AssetPackMetadata = {
		val json = io.circe.parser.parse(jsonText).toTry.get
		json.as[AssetPackMetadata].toTry.get
	}
	
}
