package cc.sukazyo.cono.morny.test.bot.event

import cc.sukazyo.cono.morny.bot.event.OnQuestionMarkReply
import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

class OnQuestionMarkReplyTest extends MornyTests with TableDrivenPropertyChecks {
	
	"on replying a question mark :" - {
		
		"on checking if a message is a question mark :" - {
			
			val examples = Table(
				("text", "is"),
				("回来了", false),
				("为什么？", false),
				("？这不合理", false),
				("??尊嘟假嘟", false),
				("?????", true),
				("?", true),
				("？", true),
				("？？❔", true),
			)
			forAll(examples) { (text, is) =>
				
				s"$text should checked with result $is" in:
					OnQuestionMarkReply.isAllMessageMark(using text) shouldEqual is
				
			}
			
		}
		
	}
	
}
