package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaType.Photo
import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaWithBytesData
import cc.sukazyo.cono.morny.extra.weibo.{genWeiboStatusUrl, MApi, MStatus, StatusUrlInfo}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.{cleanupHtml as ch, escapeHtml as h}
import io.circe.{DecodingFailure, ParsingFailure}
import sttp.client3.{HttpError, SttpClientException}

object SocialWeiboParser {
	
	@throws[HttpError[?] | SttpClientException | ParsingFailure | DecodingFailure]
	def parseMStatus (api: MApi[MStatus]): SocialContent = {
		def retweetedMessage (retweetedStatus: Option[MStatus]): String =
			retweetedStatus match
				case Some(status) =>
					val pic_preview = if status.pic_ids.isEmpty then "" else
						"\n" + (for (pic <- status.pic_ids) yield "🖼️").mkString(" ")
					// language=html
					s"""
					   |<i>//<a href="https://weibo.com/${status.user.id}/${status.id}">${h(status.user.screen_name)}</a>:</i>
					   |${ch(status.text)}$pic_preview
					   |""".stripMargin
				case None => ""
		val content =
		// language=html
			s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
			   |
			   |${ch(api.data.text)}
			   |${retweetedMessage(api.data.retweeted_status)}
			   |<i><a href="${genWeiboStatusUrl(StatusUrlInfo(api.data.user.id.toString, api.data.id))}">${h(api.data.created_at)}</a></i>""".stripMargin
		api.data.pics match
			case None =>
				SocialContent(content, Nil)
			case Some(pics) =>
				val mediaGroup = pics.map(f => SocialMediaWithBytesData(MApi.Fetch.pic(f.large.url))(Photo))
				SocialContent(content, mediaGroup)
	}
	
}
