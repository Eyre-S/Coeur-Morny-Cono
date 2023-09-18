package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.util.OkHttpPublic.MediaTypes
import com.google.gson.Gson
import okhttp3.{OkHttpClient, Request, RequestBody, ResponseBody}

import java.io.IOException
import scala.util.Using

object NbnhhshQuery {
	
	case class Word (name: String, trans: Array[String], inputting: Array[String])
	case class GuessResult (words: Array[Word])
	
	private case class GuessRequest (text: String)
	
	private val API_URL = "https://lab.magiconch.com/api/nbnhhsh/"
	private val API_GUESS_METHOD = "guess/"
	
	private val httpClient = OkHttpClient()
	
	@throws[IOException]
	def sendGuess (text: String): GuessResult = {
		val requestJsonText = Gson().toJson(GuessRequest(text))
		val request = Request.Builder()
				.url(API_URL + API_GUESS_METHOD)
				.post(RequestBody.create(requestJsonText, MediaTypes.JSON))
				.build
		Using (httpClient.newCall(request).execute) { response =>
			val body = response.body
			if body eq null then throw IOException("Nbnhhsh Request: body is null.")
			val x = s"{ 'words': ${body.string} }"
			Gson().fromJson(x, classOf[GuessResult])
		}.get
	}
	
}
