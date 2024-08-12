package cc.sukazyo.cono.morny.test.assets

import cc.sukazyo.cono.morny.test.MornyTests
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

object BilibiliAssets {
	
	case class MessagesWithUrls (
		without_b23_url: List[MessageData],
		with_b23_url: List[MessageData]
	)
	case class MessageData (
		content: String,
		with_links: List[InMessageLink]
	)
	case class InMessageLink (
		raw: String,
		bv: String,
		shareId: Option[String]
	)
	
	implicit val codec_MessagesWithUrls: Codec[MessagesWithUrls] = deriveCodec
	implicit val codec_MessageData: Codec[MessageData] = deriveCodec
	implicit val codec_InMessageLink: Codec[InMessageLink] = deriveCodec
	lazy val message_with_urls: MessagesWithUrls =
		import io.circe.yaml.v12.parser
		parser.parse(MornyTests.assets.getFile("bilibili", "messages_with_urls.yml").readString).toTry.get
			.as[MessagesWithUrls].toTry.get
	
}
