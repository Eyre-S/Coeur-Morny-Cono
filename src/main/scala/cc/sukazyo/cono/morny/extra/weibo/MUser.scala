package cc.sukazyo.cono.morny.extra.weibo

case class MUser (
	
	id: Long,
	screen_name: String,
	profile_url: String,
	profile_image_url: Option[String],
	avatar_hd: Option[String],
	description: Option[String],
	cover_image_phone: Option[String],
	
)
