package cc.sukazyo.cono.morny.data.ip186

import okhttp3.{OkHttpClient, Request}

import java.io.IOException
import scala.language.postfixOps
import scala.util.Using

object IP186QueryHandler {
	
	private val SITE_URL = "https://ip.186526.xyz/"
	private val QUERY_PARAM_IP = "type=json&format=true"
	private val QUERY_PARAM_WHOIS = "type=plain"
	
	private val httpClient = OkHttpClient()
	
	@throws[IOException]
	def query_ip (ip: String): IP186Response =
		commonQuery(SITE_URL + ip, QUERY_PARAM_IP)
	
	@throws[IOException]
	def query_whois (domain: String): IP186Response =
		commonQuery(SITE_URL+"whois/"+domain, QUERY_PARAM_WHOIS)
	
	@throws[IOException]
	def query_whoisPretty (domain: String): IP186Response =
		val raw = query_whois(domain)
		IP186Response(raw.url, raw.body substring(0, (raw.body indexOf "<<<")+3))
	
	@throws[IOException]
	private def commonQuery (requestUrl: String, queryParam: String): IP186Response = {
		val request = Request.Builder().url(requestUrl + "?" + queryParam).build
		Using ((httpClient newCall request) execute) { response =>
			val _body = response.body
			if _body eq null then throw IOException("Response of ip186: body is empty!")
			IP186Response(requestUrl, _body.string)
		}.get
	}
	
}
