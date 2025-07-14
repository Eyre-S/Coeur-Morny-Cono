package cc.sukazyo.cono.morny.reporter

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.command.MornyInformation
import cc.sukazyo.cono.morny.core.event.{TelegramBotEvents, TelegramCoreCommandEvents}
import cc.sukazyo.cono.morny.core.module.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.module.MornyModuleInject

object ModuleMornyReporter {
	
	val id: String = "morny.report"
	val name: String = "Morny/Coeur Reporter"
	val description: String | Null =
		"""Report crucial messages to a Telegram channel.
		  |""".stripMargin
	
}

@MornyModuleInject
class ModuleMornyReporter extends MornyInternalModule {
	
	override val id: String = ModuleMornyReporter.id
	override val name: String = ModuleMornyReporter.name
	override val description: String | Null = ModuleMornyReporter.description
	
	description.take(description.indexOf("\n"))
	
	override def onInitializingPre (using MornyCoeur)(cxt: MornyCoeur.OnInitializingPreContext): Unit = {
		import cxt.*
		
		val instance = MornyReport()
		externalContext << instance
		givenCxt << instance
		
	}
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		externalContext >> { (instance: MornyReport) =>
			given MornyReport = instance
			
			logger `info` "MornyReport will now collect your bot event statistics."
			eventManager register instance.EventStatistics.EventInfoCatcher
			
			import command.*
			MornyInformation.inCoeur register Info_EventStatistic()
			
		} || {
			logger `warn` "There seems no reporter instance is provided; skipped register events for it."
		}
		
	}
	
	override def onStarting (using coeur: MornyCoeur)(cxt: MornyCoeur.OnStartingContext): Unit = {
		import coeur.externalContext
		externalContext >> { (instance: MornyReport) =>
			
			instance.start()
			
			TelegramBotEvents.inCoeur.OnGetUpdateFailed
				.registerListener(instance.botErrorsReport.onGetUpdateFailed)
			TelegramBotEvents.inCoeur.OnListenerOccursException
				.registerListener(instance.botErrorsReport.onEventListenersThrowException)
			TelegramCoreCommandEvents.inCoeur.OnUnauthorizedManageCommandCall
				.registerListener(instance.coreCommandsReports.onUnauthorizedManageCommandCall)
			
		} || {
			logger `warn` "There seems no reporter instance is provided; skipped start it."
		}
	}
	
	override def onStartingPost (using coeur: MornyCoeur)(cxt: MornyCoeur.OnStartingPostContext): Unit = {
		
		import coeur.externalContext
		
		externalContext >> { (instance: MornyReport) =>
			instance.reportCoeurMornyLogin()
		}
		
	}
	
	override def onExiting (using coeur: MornyCoeur): Unit = {
		import coeur.externalContext
		externalContext >> { (instance: MornyReport) =>
			instance.stop()
		} || {
			logger `warn` "There seems no reporter instance need to be stop."
		}
	}
	
	override def onExitingPost (using coeur: MornyCoeur): Unit = {
		import coeur.externalContext
		externalContext >> { (instance: MornyReport) =>
			instance.reportCoeurExit()
		}
	}
	
}
