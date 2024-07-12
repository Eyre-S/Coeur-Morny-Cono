package cc.sukazyo.cono.morny.social_share.external.twitter

import cc.sukazyo.cono.morny.social_share.external.twitter.FXMosaicPhoto.formatsType

/** Data for the mosaic service, which stitches photos together
  *
  * @param `type` This can help compare items in a pool of media
  * @param formats Pool of formats, only `jpeg` and `webp` are returned currently
  */
case class FXMosaicPhoto (
	`type`: "mosaic_photo",
	formats: formatsType
)

object FXMosaicPhoto {
	/**  Pool of formats, only `jpeg` and `webp` are returned currently.
	  * @param webp URL for webp resource
	  * @param jpeg URL for jpeg resource
	  */
	case class formatsType (
		webp: String,
		jpeg: String
	)
}
