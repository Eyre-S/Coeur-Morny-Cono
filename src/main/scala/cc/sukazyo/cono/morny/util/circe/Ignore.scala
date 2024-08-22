package cc.sukazyo.cono.morny.util.circe

import io.circe.{Codec, HCursor, Json}
import io.circe.Decoder.Result

case class Ignore ()

object Ignore {
	
	implicit val codec_ignore: Codec[Ignore] = new Codec[Ignore] {
		override def apply (c: HCursor): Result[Ignore] = Right(Ignore())
		override def apply (a: Ignore): Json = Json.Null
	}
	
}
