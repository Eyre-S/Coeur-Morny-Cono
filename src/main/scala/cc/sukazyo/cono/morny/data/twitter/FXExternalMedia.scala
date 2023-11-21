package cc.sukazyo.cono.morny.data.twitter

/** Data for external media, currently only video.
  * 
  * @param `type` Embed type, currently always `video`
  * @param url Video URL
  * @param height Video height in pixels
  * @param width Video width in pixels
  * @param duration Video duration in seconds
  */
case class FXExternalMedia (
	`type`: String,
	url: String,
	height: Int,
	width: Int,
	duration: Int
)
