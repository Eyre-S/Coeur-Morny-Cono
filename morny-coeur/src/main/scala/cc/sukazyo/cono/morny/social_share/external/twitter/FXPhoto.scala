package cc.sukazyo.cono.morny.social_share.external.twitter

/** This can help compare items in a pool of media
  *
  * @param `type` This can help compare items in a pool of media
  * @param url URL of the photo
  * @param width Width of the photo, in pixels
  * @param height Height of the photo, in pixels
  */
case class FXPhoto (
	`type`: "photo",
	url: String,
	width: Int,
	height: Int,
	altText: String // todo
)
