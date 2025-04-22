package cc.sukazyo.cono.morny.social_share.external.twitter

import cc.sukazyo.cono.morny.social_share.external.twitter.FXTweet.mediaType
import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochSeconds

/** The container of all the information for a Tweet.
  *
  * @param id Status (Tweet) ID
  * @param url Link to original Tweet
  * @param text Text of Tweet. May not contains some extra information like URLs.
  * @param raw_text Raw text of Tweet, contains a full article text and facets (rich information).
  *                 Comparing with the `text` field, this contains all information that you want
  *                 to know, like even the media is included.
  * @param created_at Date/Time in UTC when the Tweet was created
  * @param created_timestamp Date/Time in UTC when the Tweet was created
  * @param color Dominant color pulled from either Tweet media or from the author's profile picture.
  * @param lang Language that Twitter detects a Tweet is. May be null is unknown.
  * @param replying_to Screen name of person being replied to, or null
  * @param replying_to_status Tweet ID snowflake being replied to, or null
  * @param twitter_card Corresponds to proper embed container for Tweet, which is used by
  *                     FixTweet for our official embeds.<br>
  *                     Notice that this should be of type [["tweet"|"summary"|"summary_large_image"|"player"]]
  *                     but due to circe parser does not support it well so alternative
  *                     [[String]] type is used.
  * @param author Author of the tweet
  * @param source Tweet source (i.e. Twitter for iPhone)
  * @param likes Like count
  * @param retweets Retweet count
  * @param replies Reply count
  * @param views View count, returns null if view count is not available (i.e. older Tweets)
  * @param quote Nested Tweet corresponding to the tweet which this tweet is quoting, if applicable
  * @param pool Poll attached to Tweet
  * @param translation Translation results, only provided if explicitly asked
  * @param media Containing object containing references to photos, videos, or external media
  */
case class FXTweet (
	
	///====================
	///        Core
	///====================
	
	id: String,
	url: String,
	text: String,
	raw_text: FXRawText,
	created_at: String,
	created_timestamp: EpochSeconds,
	is_note_tweet: Boolean, // todo
	possibly_sensitive: Option[Boolean], // todo
	color: Option[String],
	lang: Option[String],
	replying_to: Option[String],
	replying_to_status: Option[String],
//	twitter_card: "tweet"|"summary"|"summary_large_image"|"player",
	twitter_card: Option[String],
	author: FXAuthor,
	source: String,
	
	///====================
	/// Interaction counts
	///====================
	
	likes: Int,
	retweets: Int,
	replies: Int,
	views: Option[Int],
	
	///====================
	///        Embeds
	///====================
	
	quote: Option[FXTweet],
	pool: Option[FXPool],
	translation: Option[FXTranslate],
	media: Option[mediaType]
	
)

object FXTweet {
	/**  Containing object containing references to photos, videos, or external media.
	  *
	  * @param external Refers to external media, such as YouTube embeds
	  * @param photos An Array of photos from a Tweet
	  * @param videos An Array of videos from a Tweet
	  * @param mosaic Corresponding Mosaic information for a Tweet
	  */
	case class mediaType (
		all: Option[List[Any]], // todo
		external: Option[FXExternalMedia],
		photos: Option[List[FXPhoto]],
		videos: Option[List[FXVideo]],
		mosaic: Option[FXMosaicPhoto]
	)
}
