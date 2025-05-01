package cc.sukazyo.cono.morny.extra.twitter

/** The facets (rich text indicators) of the text.
  *
  * Every facet is a part of the original text, contains some extra information about the
  * text segment.
  *
  * @param `type` The type of the facet.
  *               This may be one of the following (not fully listed):
  *
  *               - `hashtag` - A hashtag (like #topic)
  *               - `media` - A media, like a photo or a video.
  *
  * @param indices The indices of the text segment in the original text.
  *
  *                This should always be a list of two integers, the first one is the start
  *                index, and the second one is the end index of the text
  *                segment in the raw_text's text.
  *
  * @param original The original text of the facet.
  *                 Should be the same with texts that the indices point to.
  *
  *                 The content type is different for each type:
  *                 - for `media`, this is a media shortcode (like `t.co/abcde`).
  *                 - for `hashtag`, this is the hashtag name without hash char (like `topic`,
  *                   but not `#topic`).
  *
  * @param replacement Alternative method to show this facet.
  *
  *                    For now, only `media` have this field, and it is a URL that points to
  *                    the media in the tweet (`https://x.com/user/status/123/photo/1`).
  *
  * @param display The display text of the facet.
  *
  *                For now, only `media` have this field, and it seems like the permanent URL
  *                of the media (`pic.x.com/abcde`).
  *
  * @param id A very large integer ID of the facet. Seems only `media` have this field.
  */
case class FXFacet (
	`type`: String,
	indices: List[Int],
	original: Option[String],
	replacement: Option[String],
	display: Option[String],
	id: Option[String],
)
