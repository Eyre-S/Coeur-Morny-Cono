package cc.sukazyo.cono.morny.data.twitter

import cc.sukazyo.cono.morny.util.SttpPublic
import cc.sukazyo.cono.morny.util.SttpPublic.mornyBasicRequest
import io.circe.{DecodingFailure, ParsingFailure}

/** The struct of FixTweet Status-Fetch-API response.
  *
  * It may have some issues due to the API reference from FixTweet
  * project is very outdated and inaccurate.
  *
  * @see [[https://github.com/FixTweet/FixTweet/wiki/Status-Fetch-API]]
  *
  * @param code Status code, normally be [[200]], but can be 401
  *             or [[404]] or [[500]] due to different reasons.
  *
  *             Related to [[message]]
  * @param message Status message.
  *
  *                 - When [[code]] is [[200]], it should be `OK`
  *                 - When [[code]] is [[401]], it should be `PRIVATE_TWEET`,
  *                   while in practice, it seems PRIVATE_TWEET will
  *                   just return [[404]].
  *                 - When [[code]] is [[404]], it should be `NOT_FOUND`
  *                 - When [[code]] is [[500]], it should be `API_FILE`
  * @param tweet [[FXTweet]] content.
  * @since 1.3.0
  * @version 2023.11.21
  */
case class FXApi (
	code: Int,
	message: String,
	tweet: Option[FXTweet]
)

object FXApi {
	
	object CirceADTs {
		import io.circe.Decoder
		import io.circe.generic.semiauto.deriveDecoder
		implicit val decoderForAny: Decoder[Any] = _ => Right(None)
		implicit val decoder_FXAuthor_website: Decoder[FXAuthor.websiteType] = deriveDecoder
		implicit val decoder_FXAuthor: Decoder[FXAuthor] = deriveDecoder
		implicit val decoder_FXExternalMedia: Decoder[FXExternalMedia] = deriveDecoder
		implicit val decoder_FXMosaicPhoto_formats: Decoder[FXMosaicPhoto.formatsType] = deriveDecoder
		implicit val decoder_FXMosaicPhoto: Decoder[FXMosaicPhoto] = deriveDecoder
		implicit val decoder_FXPhoto: Decoder[FXPhoto] = deriveDecoder
		implicit val decoder_FXVideo: Decoder[FXVideo] = deriveDecoder
		implicit val decoder_FXPoolChoice: Decoder[FXPoolChoice] = deriveDecoder
		implicit val decoder_FXPool: Decoder[FXPool] = deriveDecoder
		implicit val decoder_FXTranslate: Decoder[FXTranslate] = deriveDecoder
		implicit val decoder_FXTweet_media: Decoder[FXTweet.mediaType] = deriveDecoder
		implicit val decoder_FXTweet: Decoder[FXTweet] = deriveDecoder
		implicit val decoder_FXApi: Decoder[FXApi] = deriveDecoder
	}
	
	object Fetch {
		
		import io.circe.parser
		import CirceADTs.*
		import sttp.client3.*
		import sttp.client3.okhttp.OkHttpSyncBackend
		
		val uri_base = uri"https://api.fxtwitter.com/"
		/** Endpoint URI of [[https://github.com/FixTweet/FixTweet/wiki/Status-Fetch-API FixTweet Status Fetch API]]. */
		val uri_status =
			(screen_name: Option[String], id: String, translate_to: Option[String]) =>
				uri"$uri_base/$screen_name/status/$id/$translate_to"
		
		private val httpClient = OkHttpSyncBackend()
		
		/** Get tweet data from [[uri_status FixTweet Status Fetch API]].
		  *
		  * This method uses [[SttpPublic.Headers.UserAgent.MORNY_CURRENT Morny HTTP User-Agent]]
		  *
		  * @param screen_name The screen name (@ handle) (aka. user id) of the
		  *                    tweet, which is ignored.
		  * @param id The ID of the status (tweet)
		  * @param translate_to 2 letter ISO language code of the language you
		  *                     want to translate the tweet into.
		  * @throws SttpClientException When HTTP Request fails due to network
		  *                             or else HTTP client related problem.
		  * @throws ParsingFailure When the response from API is not a regular JSON
		  *                        so cannot be parsed. It mostly due to some problem
		  *                        or breaking changes from the API serverside.
		  * @throws DecodingFailure When cannot decode the API response to a [[FXApi]]
		  *                         object. It might be some wrong with the [[FXApi]]
		  *                         model, or the remote API spec changes.
		  * @return a [[FXApi]] response object, with [[200]] or any other response code.
		  */
		@throws[SttpClientException|ParsingFailure|DecodingFailure]
		def status (screen_name: Option[String], id: String, translate_to: Option[String] = None): FXApi =
			val get = mornyBasicRequest
				.get(uri_status(screen_name, id, translate_to))
				.response(asString)
				.send(httpClient)
			val body = get.body match
				case Left(error) => error
				case Right(success) => success
			parser.parse(body)
				.toTry.get
				.as[FXApi]
				.toTry.get
		
	}
	
}
