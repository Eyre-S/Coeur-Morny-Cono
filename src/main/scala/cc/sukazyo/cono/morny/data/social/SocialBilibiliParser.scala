package cc.sukazyo.cono.morny.data.social

import cc.sukazyo.cono.morny.extra.BilibiliForms.BiliVideoId
import cc.sukazyo.cono.morny.extra.bilibili.XWebView
import cc.sukazyo.cono.morny.util.CommonFormat.formatDurationTimers
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h

import cc.sukazyo.cono.morny.util.StringEnsure.ensureNotExceed

import java.time.Duration

object SocialBilibiliParser {
	
	def printsBilibiliVideoCaption (vid: BiliVideoId, info: XWebView): String =
		// language=html
		s"""<a href="https://www.bilibili.com/video/av${vid.av}"><b>${h(info.title)}</b></a>
		   |  <i>${formatDurationTimers(Duration.ofSeconds(info.duration))}</i>  <a href="https://space.bilibili.com/${info.owner.mid}">@${h(info.owner.name)}</a>
		   |
		   |${h(info.desc.ensureNotExceed(900))}""".stripMargin
	
}
