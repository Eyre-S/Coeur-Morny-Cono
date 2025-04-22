package cc.sukazyo.cono.morny.social_share.external.twitter

/** The raw text of the tweet.
  * 
  * Contains all information that you want to know about the tweet content.
  * 
  * @param text The text-formatted tweet content. Medias is also attached as a URL in the text.
  * @param facets The facets (rich text information) of the text.
  */
case class FXRawText (
	text: String,
	facets: List[FXFacet]
)
