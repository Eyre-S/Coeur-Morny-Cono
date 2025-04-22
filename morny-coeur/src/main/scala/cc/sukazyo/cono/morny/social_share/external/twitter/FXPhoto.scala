package cc.sukazyo.cono.morny.social_share.external.twitter

/** This can help compare items in a pool of media
  *
  * @param `type` This can help compare items in a pool of media
  * @param url URL of the photo
  * @param width Width of the photo, in pixels
  * @param height Height of the photo, in pixels
  * @param altText Alternative text of the photo, or also known as photo description.
  *
  *                It seems that this is not provided by Fix-Twitter API after 2025-04.
  */
case class FXPhoto (
	`type`: "photo",
	url: String,
	width: Int,
	height: Int,
	// todo: Find a tweet to test if this can still work
	altText: Option[String]
)
