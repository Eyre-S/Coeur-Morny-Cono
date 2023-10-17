package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.util.BiliTool
import cc.sukazyo.cono.morny.util.UseSelect.select
import okhttp3.{HttpUrl, OkHttpClient, Request}

import java.io.IOException
import scala.util.matching.Regex
import scala.util.Using

object BilibiliForms {
	
	case class BiliVideoId (av: Long, bv: String, part: Int|Null = null)
	
	private val REGEX_BILI_ID = "^((?:av|AV)(\\d{1,12})|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]{10}))$"r
	private val REGEX_BILI_V_PART_IN_URL_PARAM = "(?:&|^)p=(\\d+)"r
	private val REGEX_BILI_VIDEO: Regex = "^(?:(?:https?://)?(?:(?:www\\.)?bilibili\\.com(?:/s)?/video/|b23\\.tv/)((?:av|AV)(\\d{1,12})|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]{10}))/?(?:\\?((?:p=(\\d+))?.*))?|(?:av|AV)(\\d{1,12})|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]{10}))$" r
	
	/** parse a Bilibili video link to a [[BiliVideoId]] format Bilibili Video Id.
	  *
	  * @param url the Bilibili video link -- should be a valid link with av/BV,
	  *            can take some tracking params (will be ignored), can be a search
	  *            result link (have `s/` path).
	  *            Or, it can also be a b23 video link: starts with b23.tv hostname with
	  *            no www. prefix, and no /video/ path.
	  * @throws IllegalArgumentException when the link is not the valid bilibili video link
	  * @return the [[BiliVideoId]] contains raw or converted av id, and raw or converted bv id,
	  *         and video part id.
	  */
	@throws[IllegalArgumentException]
	def parse_videoUrl (url: String): BiliVideoId =
		url match
			case REGEX_BILI_VIDEO(_url_v, _url_av, _url_bv, _url_param, _url_v_part, _raw_av, _raw_bv) =>
				
				val av = select(_url_av, _raw_av)
				val bv = select(_url_bv, _raw_bv)
				
				val part_part = if (_url_param == null) null else
					REGEX_BILI_V_PART_IN_URL_PARAM.findFirstMatchIn(_url_param) match
						case Some(part) => part.group(1)
						case None => null
				val part: Int | Null = if (part_part != null) part_part toInt else null
				
				if (av == null) {
					assert(bv != null)
					BiliVideoId(BiliTool.toAv(bv), bv, part)
				} else {
					val _av = av.toLong
					BiliVideoId(_av, BiliTool.toBv(_av), part)
				}
				
			case _ => throw IllegalArgumentException(s"not a valid Bilibili video link: $url")
	
	private val httpClient = OkHttpClient
		.Builder()
		.followSslRedirects(true)
		.followRedirects(false)
		.build()
	
	/** get the bilibili video url from b23.tv share url.
	  *
	  * result url can be used in [[parse_videoUrl]]
	  *
	  * @param url b23.tv share url.
	  * @throws IllegalArgumentException the input `url` is not a b23.tv url
	  * @throws IllegalStateException some exception occurred when getting information from remote
	  *                               host, or failed to parse the information got
	  * @return bilibili video url with tracking params
	  */
	@throws[IllegalStateException|IllegalArgumentException]
	def destructB23Url (url: String): String =
		val _url: HttpUrl = HttpUrl.parse(
			if url startsWith "http://" then url.replaceFirst("http://", "https://") else url
		)
		if _url == null then throw IllegalArgumentException("not a valid url: " + url)
		if _url.host != "b23.tv" then throw IllegalArgumentException(s"not a b23 share link: $url")
		if (!_url.pathSegments.isEmpty) && _url.pathSegments.get(0).matches(REGEX_BILI_ID.regex) then
			throw IllegalArgumentException(s"is a b23 video link: $url ; (use parse_videoUrl directly)")
		val result: Option[String] =
			try {
				Using(httpClient.newCall(Request.Builder().url(_url).build).execute()) { response =>
					if response.isRedirect then
						val _u = response header "Location"
						if _u != null then
							Some(_u)
						else throw IllegalStateException("unable to get b23.tv redir location from: " + response)
					else throw IllegalStateException("unable to get b23.tv redir location from: " + response)
				}.get
			} catch case e: IOException =>
				throw IllegalStateException("get b23.tv failed.", e)
		result match
			case Some(_result) => _result
			case None => throw IllegalStateException("unable to parse from b23.tv .")
	
}
