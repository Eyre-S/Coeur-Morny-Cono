package cc.sukazyo.cono.morny.extra.weibo

case class MStatus (
	
	id: String,
	mid: String,
	bid: String,
	
	created_at: String,
	text: String,
	raw_text: Option[String],
	
	user: MUser,
	
	retweeted_status: Option[MStatus],
	
	pic_ids: List[String],
	pics: Option[List[MPic]],
	thumbnail_pic: Option[String],
	bmiddle_pic: Option[String],
	original_pic: Option[String],
	
//	visible: Nothing,
//	created_at: String,
//	id: String,
//	mid: String,
//	bid: String,
//	can_edit: Boolean,
//	show_additional_indication: Int,
//	text: String,
//	textLength: Option[Int],
//	source: String,
//	favorited: Boolean,
//	pic_ids: List[String],
//	pic_focus_point: Option[List[Nothing]],
//	falls_pic_focus_point: Option[List[Nothing]],
//	pic_rectangle_object: Option[List[Nothing]],
//	pic_flag: Option[Int],
//	thumbnail_pic: Option[String],
//	bmiddle_pic: Option[String],
//	original_pic: Option[String],
//	is_paid: Boolean,
//	mblog_vip_type: Int,
//	user: Nothing,
//	picStatus: Option[String],
//	retweeted_status: Option[Nothing],
//	reposts_count: Int,
//	comments_count: Int,
//	reprint_cmt_count: Int,
//	attitudes_count: Int,
//	pending_approval_count: Int,
//	isLongText: Boolean,
//	show_mlevel: Int,
//	topic_id: Option[String],
//	sync_mblog: Option[Boolean],
//	is_imported_topic: Option[Boolean],
//	darwin_tags: List[Nothing],
//	ad_marked: Boolean,
//	mblogtype: Int,
//	item_category: String,
//	rid: String,
//	number_display_strategy: Nothing,
//	content_auth: Int,
//	safe_tags: Option[Int],
//	comment_manage_info: Nothing,
//	repost_type: Option[Int],
//	pic_num: Int,
//	jump_type: Option[Int],
//	hot_page: Nothing,
//	new_comment_style: Int,
//	ab_switcher: Int,
//	mlevel: Int,
//	region_name: String,
//	region_opt: 1,
//	page_info: Option[Nothing],
//	pics: Option[List[Nothing]],
//	raw_text: Option[String],
//	buttons: List[Nothing],
//	status_title: Option[String],
//	ok: Int,
	
	
//	pid: Long,
//	pidstr: String,
//	pic_types: String,
//	alchemy_params: Nothing,
//	ad_state: Int,
//	cardid: String,
//	hide_flag: Int,
//	mark: String,
//	more_info_type: Int,
)
