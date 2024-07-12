package cc.sukazyo.cono.morny.social_share.api

import cc.sukazyo.cono.morny.social_share.api.SocialContent.SocialMediaType.Photo
import cc.sukazyo.cono.morny.social_share.api.SocialContent.SocialMediaWithBytesData
import cc.sukazyo.cono.morny.social_share.external.weibo.{genWeiboStatusUrl, MApi, MStatus, StatusUrlInfo}
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.{cleanupHtml as ch, escapeHtml as h}
import io.circe.{DecodingFailure, ParsingFailure}
import sttp.client3.{HttpError, SttpClientException}

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
	def parseMStatus (api: MApi[MStatus]): SocialContent = {
		val content =
			// language=html
			s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
			   |
			   |${ch(api.data.text)}
			   |${parseMStatus_forRetweeted(api.data)}
			   |<i><a href="${genWeiboStatusUrl(StatusUrlInfo(api.data.user.id.toString, api.data.id))}">${h(api.data.created_at)}</a></i>""".stripMargin
		val content_withPicPlaceholder =
		// language=html
			s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
			   |
			   |${ch(api.data.text)}${parseMStatus_forPicPreview(api.data)}
			   |${parseMStatus_forRetweeted(api.data)}
			   |<i><a href="${genWeiboStatusUrl(StatusUrlInfo(api.data.user.id.toString, api.data.id))}">${h(api.data.created_at)}</a></i>""".stripMargin
		api.data.pics match
			case None =>
				SocialContent(content, content_withPicPlaceholder, Nil)
			case Some(pics) =>
				val mediaGroup = pics.map(f => SocialMediaWithBytesData(MApi.Fetch.pic(f.large.url))(Photo))
				SocialContent(content, content_withPicPlaceholder, mediaGroup)
	}
	
}
