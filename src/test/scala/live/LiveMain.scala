package live

import cc.sukazyo.cono.morny.bot.api.EventEnv
import cc.sukazyo.cono.morny.test.utils.BiliToolTest

@main def LiveMain (args: String*): Unit = {
	
	val env: EventEnv = EventEnv(null)
	
	env provide "abcdefg"
	
	env use classOf[String] consume { (str: String) => println(str) } onfail { println("no str found in the env") }
	
}
