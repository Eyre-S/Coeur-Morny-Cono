package cc.sukazyo.cono.morny.extra

import scala.util.matching.Regex

package object twitter {
	
	private val REGEX_TWEET_URL: Regex = "^(?:https?://)?((?:(?:(?:c\\.)?vx|fx|www\\.)?twitter|(?:www\\.|fixup|fixv)?x)\\.com)/((\\w+)/status/(\\d+)(?:/photo/(\\d+))?)/?(?:\\?([\\w&=-]+))?$"r
	
	/** Messages that can contains on a tweet url.
	  *
	  * A tweet url is like `https://twitter.com/pj_sekai/status/1726526899982352557?s=20`
	  * which can be found in address bar of tweet page or tweet's share link.
	  *
	  * @param domain Domain of the tweet url. Normally `twitter.com` or `x.com`
	  *               (can be with `www.` or without). But [[parseTweetUrl]] also
	  *               supports to parse some third-party tweet share url domain
	  *               includes `fx.twitter.com`, `vxtwitter.com`(or `c.vxtwitter.com`
	  *               though it have been deprecated), or `fixupx.com`.
	  * @param statusPath Full path of the status. It should be like
	  *                   `$screenName/status/$statusId`, with or without photo param
	  *                   like `/photo/$subPhotoId`. It does not contains tracking
	  *                   or any else params.
	  * @param screenName Screen name of the tweet author, aka. author's user id.
	  *                   For most case this section is useless in processing at
	  *                   the backend (because [[statusId]] along is accurate enough)
	  *                   so it may not be right, but it should always exists.
	  * @param statusId Unique ID of the status. It is unique in whole Twitter globe.
	  *                 Should be a number.
	  * @param subPhotoId photo id or serial number in the status. Unique in the status
	  *                   globe, only exists when specific a photo in the status. It should
	  *                   be a number of 0~3 (because twitter supports 4 image at most in
	  *                   one tweet).
	  * @param trackingParam All of encoded url params. Normally no data here is something
	  *                      important.
	  */
	case class TweetUrlInformation (
		domain: String,
		statusPath: String,
		screenName: String,
		statusId: String,
		subPhotoId: Option[String],
		trackingParam: Option[String]
	)
	
	/** Parse a url to [[TweetUrlInformation]] for future processing.
	  *
	  * Supports following url:
	  *
	  *  - `twitter.com` or `www.twitter.com`
	  *  - `x.com` or `www.x.com`
	  *  - `fxtwitter.com` or `fixupx.com`
	  *  - `vxtwitter.com` or `c.vxtwitter.com` or `fixvx.com`
	  *  - should be the path of `/:screenName/status/:id`
	  *  - can contains `./photo/:photoId`
	  *  - url param non-sensitive
	  *  - http or https non-sensitive
	  *
	  * @param url a supported tweet URL or not.
	  * @return [[Option]] with [[TweetUrlInformation]] if the input url is a supported
	  *         tweet url, or [[None]] if it's not.
	  */
	def parseTweetUrl (url: String): Option[TweetUrlInformation] =
		url match
			case REGEX_TWEET_URL(_1, _2, _3, _4, _5, _6) =>
				Some(TweetUrlInformation(
					_1, _2, _3, _4,
					Option(_5),
					Option(_6)
				))
			case _ => None
	
}
