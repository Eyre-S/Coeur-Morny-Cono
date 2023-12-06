package cc.sukazyo.cono.morny.extra.ip186

import cc.sukazyo.cono.morny.util.SttpPublic.{mornyBasicRequest, Schemes}
import sttp.client3.{asString, HttpError, SttpClientException, UriContext}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.Uri

import java.io.IOException
import scala.language.postfixOps

object IP186QueryHandler {
	
	private val SITE_HOST = "ip.186526.xyz"
	private val QUERY_PARAM_IP = Map("type" -> "json", "format" -> "true")
	private val QUERY_PARAM_WHOIS = Map("type" -> "plain")
	
	private val httpClient = OkHttpSyncBackend()
	
	@throws[IOException]
	def query_ip (ip: String): IP186Response =
		commonQuery(uri"/$ip?$QUERY_PARAM_IP")
	
	@throws[IOException]
	//noinspection ScalaWeakerAccess
	def query_whois (domain: String): IP186Response =
		commonQuery(uri"/whois/$domain?$QUERY_PARAM_WHOIS")
	
	@throws[IOException]
	def query_whoisPretty (domain: String): IP186Response =
		val raw = query_whois(domain)
		IP186Response(raw.url, raw.body substring(0, (raw.body indexOf "<<<")+3))
	
	@throws[IOException]
	private def commonQuery (requestPath: Uri): IP186Response = {
		try
			val uri = requestPath.scheme(Schemes.HTTPS).host(SITE_HOST)
			IP186Response(
				uri.toString,
				mornyBasicRequest
					.get(uri)
					.response(asString.getRight)
					.send(httpClient)
					.body
			)
		catch
			case e: SttpClientException =>
				throw IOException("request to ip186 failed: " + e.getMessage, e)
			case e: HttpError[_] =>
				throw IOException("failed get from ip186: " + e.getMessage, e)
//		val request = Request.Builder().url(requestUrl + "?" + queryParam).build
//		Using ((httpClient newCall request) execute) { response =>
//			val _body = response.body
//			if _body eq null then throw IOException("Response of ip186: body is empty!")
//			IP186Response(requestUrl, _body.string)
//		}.get
	}
	
}
