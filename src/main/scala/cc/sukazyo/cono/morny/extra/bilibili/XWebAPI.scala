package cc.sukazyo.cono.morny.extra.bilibili

import cc.sukazyo.cono.morny.extra.BilibiliForms.BiliVideoId
import cc.sukazyo.cono.morny.util.SttpPublic.mornyBasicRequest
import sttp.client3.{asString, RequestT}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.Uri

object XWebAPI {
	
	private val URL_BASE = "https://api.bilibili.com/x/web-interface"
	
	private lazy val http_client = OkHttpSyncBackend()
	
	def get_view (video: BiliVideoId)(using
			http_client: sttp.client3.SttpBackend[sttp.client3.Identity, _] = http_client,
			basic_request:  RequestT[sttp.client3.Empty, Either[String, String], Any] = mornyBasicRequest
	): XWebResponse[XWebView] = {
		
		val request_url = Uri.unsafeParse(URL_BASE)
			.addPath("view")
			.addParams("aid" -> video.av.toString)
		
		val response = basic_request
			.get(request_url)
			.response(asString.getRight)
			.send(http_client)
		
		val response_body = response.body
		
		io.circe.parser.parse(response_body)
			.toTry.get
			.as[XWebResponse[XWebView]]
			.toTry.get
		
	}
	
}
