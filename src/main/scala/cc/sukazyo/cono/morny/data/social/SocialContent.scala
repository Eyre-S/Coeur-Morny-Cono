package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.data.social.SocialContent.{SocialMedia, SocialMediaType, SocialMediaWithUrl}
import cc.sukazyo.cono.morny.data.social.SocialContent.SocialMediaType.{Photo, Video}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.query.InlineQueryUnit
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.request.*
import com.pengrad.telegrambot.request.{SendMediaGroup, SendMessage}

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
		(
			if (medias_mosaic nonEmpty) && (medias_mosaic.get.t == Photo) && medias_mosaic.get.isInstanceOf[SocialMediaWithUrl] then
				// It has multi medias, and the mosaic version is provided.
				InlineQueryUnit(InlineQueryResultPhoto(
					s"[$id_head/photo/mosaic]$id_param",
					medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url,
					thumbnailOrElse(medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url)
				).title(
					s"$name $title"
				).description(
					s"Pictures are combined. $description"
				).caption(
					text_html
				).parseMode(ParseMode.HTML)) :: Nil
			else if (medias nonEmpty) && (medias.head.t == Photo) then
				val media = medias.head
				media match
					case media_url: SocialMediaWithUrl =>
						// the medias is provided, and the first one is in URL format.
						//   it may still contain multiple medias.
						//   although in only two implementations, the Twitter implementation will always give a mosaic
						//   pic; and the Weibo implementation never uses URL formatted medias.
						InlineQueryUnit(InlineQueryResultPhoto(
							s"[$id_head/photo/0]$id_param",
							media_url.url,
							thumbnailOrElse(media_url.url)
						).title(
							s"$name $title"
						).description(
							s"Pic 1. $description"
						).caption(
							text_html
						).parseMode(ParseMode.HTML)) :: Nil
					case _ =>
						// the medias are provided but are not in URL format.
						//   in this case, the plain text version will be used.
						InlineQueryUnit(InlineQueryResultArticle(
							s"[$id_head/text_only]$id_param",
							s"$name $title",
							InputTextMessageContent(text_withPicPlaceholder).parseMode(ParseMode.HTML)
						).description(
							s"Plain text only. $description"
						)) :: Nil
			else
				// There are never any medias.
				InlineQueryUnit(InlineQueryResultArticle(
					s"[$id_head/text]$id_param",
					s"$name $title",
					InputTextMessageContent(text_html).parseMode(ParseMode.HTML)
				).description(
					description
				)) :: Nil
		) ::: Nil
	}
	
}

object SocialContent {
	
	enum SocialMediaType:
		case Photo
		case Video
	sealed trait SocialMedia(val t: SocialMediaType) {
		def genTelegramInputMedia: InputMedia[?]
	}
	case class SocialMediaWithUrl (url: String)(t: SocialMediaType) extends SocialMedia(t) {
		override def genTelegramInputMedia: InputMedia[_] =
			t match
				case Photo => InputMediaPhoto(url)
				case Video => InputMediaVideo(url)
	}
	case class SocialMediaWithBytesData (data: Array[Byte])(t: SocialMediaType) extends SocialMedia(t) {
		override def genTelegramInputMedia: InputMedia[_] =
			t match
				case Photo => InputMediaPhoto(data)
				case Video => InputMediaVideo(data)
	}
	
}
