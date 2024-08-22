package cc.sukazyo.cono.morny.extra.bilibili

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochSeconds
import cc.sukazyo.cono.morny.util.circe.Ignore
import io.circe.Codec

case class XWebView (
	bvid: String,
	aid: Long,
	videos: Int,
	tid: Int,
	tname: String,
	copyright: Int,
	pic: String,
	title: String,
	pubdate: EpochSeconds,
	ctime: EpochSeconds,
	desc: String,
	desc_v2: List[Ignore],
	state: Int,
	duration: Int,
	forward: Option[Ignore],
	mission_id: Option[Ignore],
	redirect_url: Option[Ignore],
	rights: Option[Ignore],
	owner: XWebView.User,
	stat: Ignore,
	dynamic: String,
	cid: Int,
	dimension: Ignore,
	premiere: Ignore,
	teenage_mode: Int,
	is_chargeable_season: Boolean,
	is_story: Boolean,
	no_cache: Boolean,
	pages: List[Ignore],
	subtitle: Ignore,
	staff: Option[List[Ignore]],
	is_season_display: Boolean,
	use_grab: Option[Ignore],
	honor_reply: Option[Ignore],
	like_icon: Option[String],
	argue_info: Option[Ignore]
)

object XWebView {
	
	case class User (
		mid: Long,
		name: String,
		face: String,
	)
	
	import io.circe.generic.semiauto.deriveCodec
	implicit val codec: Codec[XWebView] = deriveCodec
	implicit val codec_User: Codec[User] = deriveCodec
	implicit val codec_with_XWebResponse: Codec[XWebResponse[XWebView]] = deriveCodec
	
}
