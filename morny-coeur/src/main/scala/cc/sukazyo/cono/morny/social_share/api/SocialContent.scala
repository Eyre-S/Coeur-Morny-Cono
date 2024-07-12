package cc.sukazyo.cono.morny.social_share.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.social_share.api.SocialContent.{SocialMedia, SocialMediaType, SocialMediaWithUrl}
import cc.sukazyo.cono.morny.social_share.api.SocialContent.SocialMediaType.{Photo, Video}
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.InlineQueryUnit
import com.pengrad.telegrambot.model.request.*
import com.pengrad.telegrambot.request.{SendMediaGroup, SendMessage}
import com.pengrad.telegrambot.TelegramBot

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
	text_html: String,
	text_withPicPlaceholder: String,
	medias: List[SocialMedia],
	medias_mosaic: Option[SocialMedia] = None,
	thumbnail: Option[SocialMedia] = None
) {
	
	def thumbnailOrElse[T] (orElse: T): String | T =
		thumbnail match
			case Some(x) if x.isInstanceOf[SocialMediaWithUrl] && x.t == Photo =>
				x.asInstanceOf[SocialMediaWithUrl].url
			case _ => orElse
	
	def outputToTelegram (using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Unit = {
		given TelegramBot = coeur.account
		if medias isEmpty then
			SendMessage(replyChat, text_html)
				.parseMode(ParseMode.HTML)
				.replyToMessageId(replyToMessage)
				.unsafeExecute
		else
			val mediaGroup = medias.map(f => f.genTelegramInputMedia)
			mediaGroup.head.caption(text_html)
			mediaGroup.head.parseMode(ParseMode.HTML)
			SendMediaGroup(replyChat, mediaGroup*)
				.replyToMessageId(replyToMessage)
				.unsafeExecute
	}
	
	def genInlineQueryResults (using id_head: String, id_param: Any, name: String): List[InlineQueryUnit[?]] = {
		(
			if (medias_mosaic nonEmpty) && (medias_mosaic.get.t == Photo) && medias_mosaic.get.isInstanceOf[SocialMediaWithUrl] then
				InlineQueryUnit(InlineQueryResultPhoto(
					inlineQueryId(s"[$id_head/photo/mosaic]$id_param"),
					medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url,
					thumbnailOrElse(medias_mosaic.get.asInstanceOf[SocialMediaWithUrl].url)
				).title(s"$name").caption(text_html).parseMode(ParseMode.HTML)) :: Nil
			else if (medias nonEmpty) && (medias.head.t == Photo) then
				val media = medias.head
				media match
					case media_url: SocialMediaWithUrl =>
						InlineQueryUnit(InlineQueryResultPhoto(
							inlineQueryId(s"[$id_head/photo/0]$id_param"),
							media_url.url,
							thumbnailOrElse(media_url.url)
						).title(s"$name").caption(text_html).parseMode(ParseMode.HTML)) :: Nil
					case _ =>
						InlineQueryUnit(InlineQueryResultArticle(
							inlineQueryId(s"[$id_head/text_only]$id_param"), s"$name (text only)",
							InputTextMessageContent(text_withPicPlaceholder).parseMode(ParseMode.HTML)
						)) :: Nil
			else
				InlineQueryUnit(InlineQueryResultArticle(
					inlineQueryId(s"[$id_head/text]$id_param"), s"$name",
					InputTextMessageContent(text_html).parseMode(ParseMode.HTML)
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
		override def genTelegramInputMedia: InputMedia[?] =
			t match
				case Photo => InputMediaPhoto(url)
				case Video => InputMediaVideo(url)
	}
	case class SocialMediaWithBytesData (data: Array[Byte])(t: SocialMediaType) extends SocialMedia(t) {
		override def genTelegramInputMedia: InputMedia[?] =
			t match
				case Photo => InputMediaPhoto(data)
				case Video => InputMediaVideo(data)
	}
	
}
