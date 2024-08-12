package cc.sukazyo.cono.morny.test.assets

import cc.sukazyo.cono.morny.test.MornyTests
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object RandomMessages {
	
	case class TextMessage (normal_message: List[String])
	implicit val codec_TextMessage: Codec[TextMessage] = deriveCodec
	
	lazy val text_message: TextMessage =
		import io.circe.yaml.v12.parser
		parser.parse(MornyTests.assets.getFile("random_message", "text_message.yml").readString).toTry.get
			.as[TextMessage].toTry.get
	
}
