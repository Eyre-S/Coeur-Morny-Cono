package cc.sukazyo.cono.morny.social_share.external.twitter

/** Data for a Tweet's video
  *
  * @param `type` Returns video if video, or gif if gif. Note that on Twitter, all GIFs are MP4s.
  * @param url URL corresponding to the video file
  * @param thumbnail_url URL corresponding to the thumbnail for the video
  * @param width Width of the video, in pixels
  * @param height Height of the video, in pixels
  * @param format Video format, usually `video/mp4`
  */
case class FXVideo (
//	`type`: "video"|"gif",
	`type`: String,
	url: String,
	thumbnail_url: String,
	width: Int,
	height: Int,
	duration: Float, // todo
	format: String
)
