package cc.sukazyo.cono.morny.extra.xhs

import cc.sukazyo.cono.morny.util.SttpPublic.{mornyBasicRequest, Schemes}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.client3.{HttpError, RequestT, SttpClientException}
import sttp.model.Uri

case class XHSLink (exploreId: String) {
	
	def link =
		s"https://www.xiaohongshu.com/explore/$exploreId"
	
}

object XHSLink {
	
	private lazy val http_client = OkHttpSyncBackend()
	
	private lazy val REGEX_EXPLORER_URL = "(?:(?:https?:)?//)?(?:www\\.)?xiaohongshu\\.com/(?:explore/|discovery/item/)([a-fA-F0-9]+)/?(?:\\?.+)?"r
	private lazy val REGEX_SHARE_URL = "(?:(?:https?:)?//)?(?:www\\.)?xhslink\\.com/([a-zA-Z0-9]+)/?(?:\\?.+)?"r
	private lazy val REGEX_SHARE_TEXTS = "\uD83D\uDE06 ([0-9a-zA-Z]+) \uD83D\uDE06 (?:(?:https?:)?//)?(?:www\\.)?xhslink\\.com/([a-zA-Z0-9]+)/?"r
	
	def matchExplorerUrl (url: String): Option[XHSLink] = {
		url match
			case REGEX_EXPLORER_URL(explorerId) => Some(XHSLink(explorerId))
			case _ => None
	}
	
	def matchShareUrl (url: String): Option[ShareLink] = {
		url match
			case REGEX_SHARE_URL(shareId) => Some(ShareLink(shareId))
			case _ => None
	}
	
	def matchUrl (url: String): Option[XHSLink|ShareLink] = {
		matchExplorerUrl(url) orElse matchShareUrl(url)
	}
	
	def searchShareText (texts: String): Option[ShareLink] = {
		REGEX_SHARE_TEXTS.findFirstMatchIn(texts).map(x => ShareLink(x.group(2)))
	}
	
	case class ShareLink (shareId: String) {
		
		def link =
			s"https://xhslink.com/$shareId"
		
		/** Get the [[XHSLink xiaohongshu explorer link]] that this share link is linked to via sttp request.
		  *
		  * @param http_client the sttp http client backend that will be used. defaults is [[XHSLink]] owned backend.
		  * @param basic_request the sttp basic request that will be used. defaults is [[mornyBasicRequest]].
		  * @throws IllegalArgumentException When the XHS server does not returns a valid explorer link. Mostly maybe the
		  *                                  share url is invalid or expired.
		  * @throws IllegalStateException When cannot connect to the XHS server.
		  * @return the [[XHSLink xiaohongshu explorer link]] that this share link is linked to.
		  */
		@throws[IllegalArgumentException]
		@throws[IllegalStateException]
		def getXhsLink (using
			http_client: sttp.client3.SttpBackend[sttp.client3.Identity, _] = http_client,
			basic_request:  RequestT[sttp.client3.Empty, Either[String, String], Any] = mornyBasicRequest
		): XHSLink = {
			
			val uri = try Uri.unsafeParse(this.link).scheme(Schemes.HTTPS) catch
				case e: IllegalArgumentException => throw IllegalStateException("Cannot format this Share link to a valid request url.").initCause(e)
			
			try {
				import sttp.client3.*
				val request = basic_request
					.get(uri)
					.followRedirects(false)
					.response(ignore)
				val response = request.send(http_client)
				val redir = response.header("Location").get
				matchExplorerUrl(redir) match
					case None => throw IllegalArgumentException(s"XHS server returns a non-XHSLink url: $redir")
					case Some(link) => link
			} catch
				case e: SttpClientException => throw IllegalStateException("failed get response from xhs server.").initCause(e)
				case e: HttpError[_] => throw IllegalStateException("failed parse response from xhs server.").initCause(e)
				case e: NoSuchElementException => throw IllegalArgumentException("XHS server does not returns any url.").initCause(e)
			
		}
		
	}
	
}
