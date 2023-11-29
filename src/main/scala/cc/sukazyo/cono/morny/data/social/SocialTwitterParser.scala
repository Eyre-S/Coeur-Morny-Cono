package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.data.social.SocialContent.{SocialMedia, SocialMediaWithUrl}
import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaType.{Photo, Video}
import cc.sukazyo.cono.morny.extra.twitter.FXApi
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h

object SocialTwitterParser {
	
	def parseFXTweet (api: FXApi): SocialContent = {
		api.tweet match
			case None =>
				SocialContent(
					// language=html
					s"""❌ Fix-Tweet <code>${api.code}</code>
					   |<i>${h(api.message)}</i>""".stripMargin,
					Nil
				)
			case Some(tweet) =>
				val content: String =
				// language=html
					s"""⚪️ <b>${h(tweet.author.name)} <a href="${tweet.author.url}">@${h(tweet.author.screen_name)}</a></b>
					   |
					   |${h(tweet.text)}
					   |
					   |<i>💬${tweet.replies}   🔗${tweet.retweets}   ❤️${tweet.likes}</i>
					   |<i><a href="${tweet.url}">${h(tweet.created_at)}</a></i>""".stripMargin
				tweet.media match
					case None =>
						SocialContent(content, Nil)
					case Some(media) =>
						val mediaGroup: List[SocialMedia] =
							(
								media.photos match
									case None => List.empty
									case Some(photos) => for i <- photos yield SocialMediaWithUrl(i.url)(Photo)
							) ::: (
								media.videos match
									case None => List.empty
									case Some(videos) => for i <- videos yield SocialMediaWithUrl(i.url)(Video)
							)
						val thumbnail =
							if media.videos.nonEmpty then
								Some(SocialMediaWithUrl(media.videos.get.head.thumbnail_url)(Photo))
							else None
						val mediaMosaic = media.mosaic match
							case Some(mosaic) => Some(SocialMediaWithUrl(mosaic.formats.jpeg)(Photo))
							case None => None
						SocialContent(content, mediaGroup, mediaMosaic, thumbnail)
	}
	
}
