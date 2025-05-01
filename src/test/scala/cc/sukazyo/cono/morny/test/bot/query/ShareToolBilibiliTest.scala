package cc.sukazyo.cono.morny.test.bot.query

import cc.sukazyo.cono.morny.bot.query.{InlineQueryUnit, ShareToolBilibili}
import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.test.assets.{BilibiliAssets, RandomMessages}
import cc.sukazyo.cono.morny.test.assets.BilibiliAssets.InMessageLink
import com.pengrad.telegrambot.model.{InlineQuery, Update}
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import org.scalatest.exceptions.TestFailedException
import org.scalatest.tagobjects.{Network, Slow}

class ShareToolBilibiliTest extends MornyTests {
	
	def formatToQueryUpdate (message: String): Update =
		val inlineQuery = InlineQuery()
		val InlineQuery_query = classOf[InlineQuery].getDeclaredField("query")
		InlineQuery_query.setAccessible(true)
		InlineQuery_query.set(inlineQuery, message)
		val update = Update()
		val Update_inlineQuery = classOf[Update].getDeclaredField("inline_query")
		Update_inlineQuery.setAccessible(true)
		Update_inlineQuery.set(update, inlineQuery)
		update
	
	extension (ex: TestFailedException) {
		def addedExtraInfo (message: String): TestFailedException =
			ex.modifyMessage {
				case Some(errLog) => Some(errLog + "\n" + message)
				case None => Some(message)
			}
	}
	
	def withInfos (info: =>String)(tests: =>Any): Any =
		try tests
		catch case ex: TestFailedException => throw ex.addedExtraInfo(info)
	
	
	"when parsing texts" - {
		
		def queryResultShouldContains (queryResult: List[InlineQueryUnit[?]], shouldContains: List[InMessageLink]): Any = {
			val titles = queryResult.map(x => {
				val rx = x.result.asInstanceOf[InlineQueryResultArticle]
				val rx_title = classOf[InlineQueryResultArticle].getDeclaredField("title")
				rx_title.setAccessible(true)
				rx_title.get(rx).asInstanceOf[String]
			})
			for (shou <- shouldContains)
				titles should contain(s"[Bilibili] Video BV${shou.bv}")
		}
		
		"that contains a video id/url should contains it in result" in {
			for (messageObject <- BilibiliAssets.message_with_urls.without_b23_url) {
				withInfos(s"In Message:\n${messageObject.content.indent(2)}") {
					queryResultShouldContains(
						ShareToolBilibili().query(formatToQueryUpdate(messageObject.content)),
						messageObject.with_links
					)
				}
			}
		}
		
		"that contains a b23 video share url should contains it in result" taggedAs (Slow, Network) in {
			for (messageObject <- BilibiliAssets.message_with_urls.with_b23_url) {
				withInfos(s"In Message:\n${messageObject.content.indent(2)}") {
					queryResultShouldContains(
						ShareToolBilibili().query(formatToQueryUpdate(messageObject.content)),
						messageObject.with_links
					)
				}
			}
		}
		
		"that have no bilibili video url should returns null" in {
			for (message <- RandomMessages.text_message.normal_message) { withInfos ("In Message:\n" + message.indent(2)) {
				ShareToolBilibili().query(formatToQueryUpdate(message)) shouldEqual List.empty
			}}
		}
		
	}
	
}
