package cc.sukazyo.cono.morny.social_share.external

package object weibo {
	
	/** Information in weibo status url.
	  *
	  * @param uid Status owner's user id. should be a number.
	  * @param id Status id. Should be unique in the whole weibo.com
	  *           globe. Maybe a number format mid, or a base58-like
	  *           bid.
	  */
	case class StatusUrlInfo (
		uid: String,
		id: String
	)
	
//	case class PicUrl (
//		cdn: String,
//		mode: String,
//		pid: String
//	) {
//		def toUrl: String =
//			s"https://$cdn.singimg.cn/$mode/$pid.jpg"
//	}
	
	private val REGEX_WEIBO_STATUS_URL = "(?:https?://)?((?:www\\.|m.)?weibo\\.(?:com|cn))/(\\d+)/([0-9a-zA-Z]+)/?(?:\\?(\\S+))?"r
	
	def parseWeiboStatusUrl (url: String): Option[StatusUrlInfo] =
		url match
			case REGEX_WEIBO_STATUS_URL(_, uid, id, _) => Some(StatusUrlInfo(uid, id))
			case _ => None
	
	def guessWeiboStatusUrl (text: String): List[StatusUrlInfo] =
		REGEX_WEIBO_STATUS_URL.findAllMatchIn(text).map(matches => {
			StatusUrlInfo(matches.group(2), matches.group(3))
		}).toList
	
	def genWeiboStatusUrl (url: StatusUrlInfo): String =
		s"https://weibo.com/${url.uid}/${url.id}"
	
//	def randomPicCdn: String =
//		import scala.util.Random
//		s"wx${Random.nextInt(4)+1}"
	
}
