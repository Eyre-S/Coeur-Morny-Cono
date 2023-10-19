package cc.sukazyo.cono.morny.data

import com.google.gson.Gson
import sttp.client3.{asString, basicRequest, HttpError, SttpClientException, UriContext}
import sttp.client3.okhttp.OkHttpSyncBackend
import sttp.model.MediaType

object NbnhhshQuery {
	
	case class Word (name: String, trans: Array[String], inputting: Array[String])
	case class GuessResult (words: Array[Word])
	
	private case class GuessRequest (text: String)
	
	private val API_URL = uri"https://lab.magiconch.com/api/nbnhhsh/"
	private val API_GUESS_METHOD = uri"$API_URL/guess/"
	
	private val httpClient = OkHttpSyncBackend()
	
	@throws[HttpError[_]|SttpClientException]
	def sendGuess (text: String): GuessResult = {
		val http = basicRequest
			.body(Gson().toJson(GuessRequest(text))).contentType(MediaType.ApplicationJson)
			.post(API_GUESS_METHOD)
			.response(asString.getRight)
			.send(httpClient)
		Gson().fromJson(s"{ 'words': ${http.body} }", classOf[GuessResult])
	}
	
}
