package cc.sukazyo.cono.morny.social_share.api

import cc.sukazyo.cono.morny.social_share.api.SocialContent.{SocialMedia, SocialMediaWithUrl}
import cc.sukazyo.cono.morny.social_share.api.SocialContent.SocialMediaType.{Photo, Video}
import cc.sukazyo.cono.morny.social_share.external.twitter.{FXApi, FXTweet}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h

object SocialTwitterParser {
	
	def parseFXTweet_forMediaPlaceholderInContent (tweet: FXTweet): String =
		tweet.media match
			case None => ""
			case Some(media) =>
				"\n" + (media.photos.getOrElse(Nil).map(* => "🖼️") ::: media.videos.getOrElse(Nil).map(* => "🎞️"))
					.mkString(" ")
	
	def parseFXTweet (api: FXApi): SocialContent = {
		api.tweet match
			case None =>
				val content =
					// language=html
					s"""❌ Fix-Tweet <code>${api.code}</code>
					   |<i>${h(api.message)}</i>""".stripMargin
				SocialContent(content, content, Nil)
			case Some(tweet) =>
				val content: String =
				// language=html
					s"""⚪️ <b>${h(tweet.author.name)} <a href="${tweet.author.url}">@${h(tweet.author.screen_name)}</a></b>
					   |
					   |${h(tweet.text)}
					   |
					   |<i>💬${tweet.replies}   🔗${tweet.retweets}   ❤️${tweet.likes}</i>
					   |<i><a href="${tweet.url}">${h(tweet.created_at)}</a></i>""".stripMargin
				val content_withMediasPlaceholder: String =
				// language=html
					s"""⚪️ <b>${h(tweet.author.name)} <a href="${tweet.author.url}">@${h(tweet.author.screen_name)}</a></b>
					   |
					   |${h(tweet.text)}${parseFXTweet_forMediaPlaceholderInContent(tweet)}
					   |
					   |<i>💬${tweet.replies}   🔗${tweet.retweets}   ❤️${tweet.likes}</i>
					   |<i><a href="${tweet.url}">${h(tweet.created_at)}</a></i>""".stripMargin
				tweet.media match
					case None =>
						SocialContent(content, content_withMediasPlaceholder, Nil)
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
						SocialContent(content, content_withMediasPlaceholder, mediaGroup, mediaMosaic, thumbnail)
	}
	
}
