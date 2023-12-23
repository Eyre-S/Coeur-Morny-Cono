package cc.sukazyo.cono.morny.social_share.external.twitter

/** Information about a requested translation for a Tweet, when asked.
  *
  * @param text Translated Tweet text
  * @param source_lang 2-letter ISO language code of source language
  * @param target_lang 2-letter ISO language code of target language
  */
case class FXTranslate (
	text: String,
	source_lang: String,
	target_lang: String
)
