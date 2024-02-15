package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.core.ServerMain
import cc.sukazyo.cono.morny.util.UniversalCommand

import scala.io.StdIn

@main def MornyCLI (): Unit = {
	
	print("$ java -jar morny-coeur-\"+MornySystem.VERSION_FULL+\".jar ")
	ServerMain.main(
		UniversalCommand(StdIn readLine)
	)
	
}
