package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.MornyCoeur.*

trait MornyModule {
	
	val id: String
	val name: String
	val version: String
	
	val description: String|Null
	
	def onInitializingPre (using MornyCoeur)(cxt: OnInitializingPreContext): Unit = {}
	def onInitializing (using MornyCoeur)(cxt: OnInitializingContext): Unit = {}
	def onInitializingPost (using MornyCoeur)(cxt: OnInitializingPostContext): Unit = {}
	
	def onStarting (using MornyCoeur)(cxt: OnStartingContext): Unit = {}
	def onStartingPost (using MornyCoeur)(cxt: OnStartingPostContext): Unit = {}
	
	def onRoutineSavingData (using MornyCoeur): Unit = {}
	
	def onExiting (using MornyCoeur): Unit = {}
	def onExitingPost (using MornyCoeur): Unit = {}
	def onExited (using MornyCoeur): Unit = {}
	
}
