package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaType.Photo
import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaWithBytesData
import cc.sukazyo.cono.morny.extra.weibo.{genWeiboStatusUrl, MApi, MStatus, StatusUrlInfo}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.{cleanupHtml as ch, escapeHtml as h}
import io.circe.{DecodingFailure, ParsingFailure}
import sttp.client3.{HttpError, SttpClientException}

import cc.sukazyo.cono.morny.util.StringEnsure.ensureNotExceed

object SocialWeiboParser {
	
	def parseMStatus_forPicPreview (status: MStatus): String =
		if status.pic_ids.isEmpty then "" else
			"\n" + (for (pic <- status.pic_ids) yield "🖼️").mkString(" ")
	
	def parseMStatus_forRetweeted (originalStatus: MStatus): String =
		originalStatus.retweeted_status match
			case Some(status) =>
				// language=html
				s"""
				   |<i>//<a href="https://weibo.com/${status.user.id}/${status.id}">${h(status.user.screen_name)}</a>:</i>
				   |${ch(status.text)}${parseMStatus_forPicPreview(status)}
				   |""".stripMargin
			case None => ""
	
	@throws[HttpError[?] | SttpClientException | ParsingFailure | DecodingFailure]
	def parseMStatus (api: MApi[MStatus])(originUrl: String): SocialContent = {
		val statusUrl: String = genWeiboStatusUrl(StatusUrlInfo(api.data.user.id.toString, api.data.id))
		val content =
			// language=html
			s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
			   |
			   |${ch(api.data.text)}
			   |${parseMStatus_forRetweeted(api.data)}
			   |<i><a href="$statusUrl">${h(api.data.created_at)}</a></i>""".stripMargin
		val content_withPicPlaceholder =
		// language=html
			s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
			   |
			   |${ch(api.data.text)}${parseMStatus_forPicPreview(api.data)}
			   |${parseMStatus_forRetweeted(api.data)}
			   |<i><a href="$statusUrl">${h(api.data.created_at)}</a></i>""".stripMargin
		val title = api.data.text.ensureNotExceed(35)
		val description: String = originUrl
		api.data.pics match
			case None =>
				SocialContent(title, description, content, content_withPicPlaceholder, Nil)
			case Some(pics) =>
				val mediaGroup = pics.map(f => SocialMediaWithBytesData(MApi.Fetch.pic(f.large.url))(Photo, statusUrl))
				SocialContent(
					if title.nonEmpty then title else
						s"from ${api.data.user.screen_name}",
					description, content, content_withPicPlaceholder, mediaGroup
				)
	}
	
}
