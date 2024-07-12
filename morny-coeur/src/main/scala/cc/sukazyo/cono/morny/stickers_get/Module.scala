package cc.sukazyo.cono.morny.stickers_get

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.stickers_get.http.StickerService

class Module extends MornyInternalModule {
	
	override val id: String = "morny.stickers-get"
	override val name: String = "Morny Can Get and Provide Stickers"
	override val description: String | Null =
		// language=markdown
		"""Make Morny as a Telegram Stickers API.
		  |
		  |This module handles `/api/sticker` route that you can get a sticker
		  |by a sticker ID via HTTP request.
		  |
		  |original idea is: https://github.com/tjhorner/tstickers-api
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		httpServer register4API StickerService()
		
	}
	
}
