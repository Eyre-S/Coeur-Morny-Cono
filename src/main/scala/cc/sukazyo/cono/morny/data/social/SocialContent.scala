package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.data.social.SocialContent.{SocialMedia, SocialMediaType, SocialMediaWithUrl}
import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaType.{Photo, Video}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.query.InlineQueryUnit
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.request.*
import com.pengrad.telegrambot.request.{SendMediaGroup, SendMessage}

import scala.collection.mutable.ListBuffer

/** Model of social networks' status. for example twitter tweet or
  * weibo status.
  *
  * Can be output to Telegram.
  *
  * @param text_html Formatted HTML output of the status that can be output
  *                  directly to Telegram. Normally will contains metadata
  *                  like status' author or like count etc.
  * @param text_withPicPlaceholder same with [[text_html]], but contains more
  *                                placeholder texts of medias. can be used
  *                                when medias cannot be output.
  * @param medias Status attachment medias.
  * @param medias_mosaic Mosaic version of status medias. Will be used when
  *                      the output API doesn't support multiple medias like
  *                      Telegram inline API. This value is depends on the specific
  *                      backend parser/formatter implementation.
  * @param thumbnail Medias' thumbnail. Will be used when the output API required
  *                  a thumbnail. This value is depends on the specific backend
  *                  parser/formatter implementation.
  */
case class SocialContent (
	title: String,
	description: String,
	text_html: String,
	text_withPicPlaceholder: String,
	medias: List[SocialMedia],
	medias_mosaic: Option[SocialMedia] = None,
	thumbnail: Option[SocialMedia] = None,
) {
	
	def thumbnailOrElse[T] (orElse: T): String | T =
		thumbnail match
			case Some(x) if x.isInstanceOf[SocialMediaWithUrl] && x.t == Photo =>
				x.asInstanceOf[SocialMediaWithUrl].url
			case _ => orElse
	
	def outputToTelegram (using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Unit = {
		if medias isEmpty then
			coeur.account exec
				SendMessage(replyChat, text_html)
					.parseMode(ParseMode.HTML)
					.replyToMessageId(replyToMessage)
		else
			val mediaGroup = medias.map(f => f.genTelegramInputMedia)
			mediaGroup.head.caption(text_html)
			mediaGroup.head.parseMode(ParseMode.HTML)
			coeur.account exec
				SendMediaGroup(replyChat, mediaGroup: _*)
					.replyToMessageId(replyToMessage)
	}
	
	def genInlineQueryResults (using id_head: String, id_param: Any, name: String): List[InlineQueryUnit[?]] = {
		val results = ListBuffer[InlineQueryUnit[?]]()
		
		// It has multi medias, and the mosaic version is provided,
		//  uses the mosaic version to provide an image+text result.
		if (medias.length > 1) && (medias_mosaic nonEmpty) && (medias_mosaic.get.t == Photo) && medias_mosaic.get.isInstanceOf[SocialMediaWithUrl] then
			results +=
				InlineQueryUnit(InlineQueryResultPhoto(
					s"[$id_head/photo/mosaic]$id_param",
					medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url,
					thumbnailOrElse(medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url)
				).title(
					s"$name $title"
				).description(
					s"Medias combined. $description"
				).caption(
					text_html
				).parseMode(ParseMode.HTML))
		
		// It has medias, and the first media is URL formatted, then provide the first media with content,
		//  uses the first media to provide an image1+text result.
		else if (medias nonEmpty) && medias.head.isInstanceOf[SocialMediaWithUrl] then
			val media = medias.head.asInstanceOf[SocialMediaWithUrl]
			results +=
				InlineQueryUnit(InlineQueryResultPhoto(
					s"[$id_head/photo/0/contented]$id_param",
					media.url,
					thumbnailOrElse(media.url)
				).title(
					s"$name $title"
				).description(
					s"Pic 1/${medias.length}, with content. $description"
				).caption(
					text_html
				).parseMode(ParseMode.HTML))
		
		// It has medias, and all the previous method failed,
		//  then a plain text version will be provided.
		else if medias.nonEmpty then
			results +=
				InlineQueryUnit(InlineQueryResultArticle(
					s"[$id_head/text_only]$id_param",
					s"$name $title",
					InputTextMessageContent(text_withPicPlaceholder).parseMode(ParseMode.HTML)
				).description(
					s"Plain text. $description"
				))
		
		// The medias is provided, iterate all the medias and provide a media-only result.
		//   Note that the media are not URL formatted will be ignored.
		val resultsMediaOnly: IndexedSeq[InlineQueryUnit[?]] = if medias nonEmpty then for mediaId <- medias.indices yield {
			val media = medias(mediaId)
			media match
				case media_url: SocialMediaWithUrl =>
					InlineQueryUnit(InlineQueryResultPhoto(
						s"[$id_head/photo/$mediaId/bare]$id_param",
						media_url.url,
						thumbnailOrElse(media_url.url)
					).title(
						s"$name $title"
					).description(
						s"Pic ${mediaId+1}/${medias.length}, pic only. $description"
					).caption(
						media_url.sourceUrl
					))
				case _ => null
		} else Nil.toIndexedSeq
		results ++= resultsMediaOnly
		
		// If there are no any medias, use the plain text mode.
		if results isEmpty then
			results +=
				InlineQueryUnit(InlineQueryResultArticle(
					s"[$id_head/text]$id_param",
					s"$name $title",
					InputTextMessageContent(text_html).parseMode(ParseMode.HTML)
				).description(
					description
				))
		
		results.toList
	}
	
}

object SocialContent {
	
	enum SocialMediaType:
		case Photo
		case Video
	sealed trait SocialMedia(val t: SocialMediaType, val sourceUrl: String) {
		def genTelegramInputMedia: InputMedia[?]
	}
	case class SocialMediaWithUrl (url: String)(t: SocialMediaType, sourceUrl: String) extends SocialMedia(t, sourceUrl) {
		override def genTelegramInputMedia: InputMedia[_] =
			t match
				case Photo => InputMediaPhoto(url)
				case Video => InputMediaVideo(url)
	}
	case class SocialMediaWithBytesData (data: Array[Byte])(t: SocialMediaType, sourceUrl: String) extends SocialMedia(t, sourceUrl) {
		override def genTelegramInputMedia: InputMedia[_] =
			t match
				case Photo => InputMediaPhoto(data)
				case Video => InputMediaVideo(data)
	}
	
}
