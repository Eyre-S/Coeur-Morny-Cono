package cc.sukazyo.cono.morny.extra

import cc.sukazyo.cono.morny.util.SttpPublic.mornyBasicRequest
import com.google.gson.Gson
import sttp.client3.{asString, HttpError, SttpClientException, UriContext}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.MediaType

object NbnhhshQuery {
	
	case class Word (
		name: String,
		trans: Array[String] = Array.empty,
		inputting: Array[String] = Array.empty
	)
	case class GuessResult (words: Array[Word])
	
	private val API_URL = uri"https://lab.magiconch.com/api/nbnhhsh/"
	private val API_GUESS_METHOD = uri"$API_URL/guess/"
	
	private val httpClient = OkHttpSyncBackend()
	
	@throws[HttpError[_]|SttpClientException]
	def sendGuess (text: String): GuessResult = {
		case class GuessRequest (text: String)
		val http = mornyBasicRequest
			.body(Gson().toJson(GuessRequest(text))).contentType(MediaType.ApplicationJson)
			.post(API_GUESS_METHOD)
			.response(asString.getRight)
			.send(httpClient)
		GuessResult(Gson().fromJson(http.body, classOf[Array[Word]]))
	}
	
}
