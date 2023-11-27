package cc.sukazyo.cono.morny.data.weibo

case class MPic (
	pid: String,
	url: String,
	size: String,
	geo: MPic.geoType,
	large: MPic.largeType
)

object MPic {
	
	case class geoType (
//		width: Int,
//		height: Int,
		croped: Boolean
	)
	
	case class largeType (
		size: String,
		url: String,
		geo: largeType.geoType
	)
	
	object largeType {
		case class geoType (
//			width: String,
//			height: String,
			croped: Boolean
		)
	}
	
}
