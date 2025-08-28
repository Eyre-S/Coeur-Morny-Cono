package cc.sukazyo.cono.morny.extra.weibo

import sttp.model.HeaderNames

case class MApi [D] (
	ok: Int,
	data: D
)

object MApi {
	
	object CirceADTs {
		import io.circe.Decoder
		import io.circe.generic.semiauto.deriveDecoder
		given Decoder[MUser] = deriveDecoder
		given given_Decoder_largeType_getType: Decoder[MPic.largeType.geoType] = deriveDecoder
		given Decoder[MPic.largeType] = deriveDecoder
		given Decoder[MPic.geoType] = deriveDecoder
		given Decoder[MPic] = deriveDecoder
		given Decoder[MStatus] = deriveDecoder
		given Decoder[MApi[MStatus]] = deriveDecoder
	}
	
	object Fetch {
		
		import cc.sukazyo.cono.morny.util.SttpPublic
		import cc.sukazyo.cono.morny.util.SttpPublic.mornyBasicRequest
		import io.circe.{DecodingFailure, ParsingFailure, parser}
		import sttp.client3.okhttp.OkHttpSyncBackend
		import sttp.client3.{HttpError, SttpClientException, UriContext}
		
		val uri_base = uri"https://m.weibo.cn/"
		val uri_statuses_show =
			(id: String) => uri"$uri_base/statuses/show?id=$id"
		
		private val httpClient = OkHttpSyncBackend()
		private val basicRequest = mornyBasicRequest
			.header(HeaderNames.Referer, uri_base.toString)
		
		@throws[HttpError[_]|SttpClientException|ParsingFailure|DecodingFailure]
		def statuses_show (id: String): MApi[MStatus] =
			import MApi.CirceADTs.given
			import sttp.client3.asString
			val response = basicRequest
				.get(uri_statuses_show(id))
				.response(asString.getRight)
				.send(httpClient)
			parser.parse(response.body)
				.toTry.get
				.as[MApi[MStatus]]
				.toTry.get
		
		@throws[HttpError[_] | SttpClientException | ParsingFailure | DecodingFailure]
		def pic (picUrl: String): Array[Byte] =
			import sttp.client3.*
			import sttp.model.{MediaType, Uri}
			this.basicRequest
				.acceptEncoding(MediaType.ImageJpeg.toString)
				.get(Uri.unsafeParse(picUrl))
				.response(asByteArray.getRight)
				.send(httpClient)
				.body
		
//		@throws[HttpError[_] | SttpClientException | ParsingFailure | DecodingFailure]
//		def pic (info: PicUrl): Array[Byte] =
//			pic(info.toUrl)
		
	}
	
}
