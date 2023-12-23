package cc.sukazyo.cono.morny.medication_timer

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur

class ModuleMedicationTimer extends MornyInternalModule {
	
	override val id: String = "morny.medication_timer"
	override val name: String = "Morny Medication Timer"
	override val description: String | Null =
		"""A notify tool for Morny notify its master to take medication.
		  |""".stripMargin
	
	override def onInitializingPre (using MornyCoeur)(cxt: MornyCoeur.OnInitializingPreContext): Unit = {
		import cxt.*
		
		val instance: MedicationTimer = MedicationTimer()
		externalContext << instance
		givenCxt << instance
		
	}
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		externalContext >> { (instance: MedicationTimer) =>
			eventManager register OnMedicationNotifyApply(using instance)
		} || {
			logger warn "There seems no Medication Timer instance is provided; skipped register events for it."
		}
		
	}
	
	override def onStarting (using coeur: MornyCoeur)(cxt: MornyCoeur.OnStartingContext): Unit = {
		import coeur.*
		
		externalContext >> { (instance: MedicationTimer) =>
			instance.start()
		} || {
			logger warn "There seems no Medication Timer instance is provided; skipped start it."
		}
		
	}
	
	override def onExiting (using coeur: MornyCoeur): Unit = {
		import coeur.*
		
		externalContext >> { (instance: MedicationTimer) =>
			instance.stop()
		} || {
			logger warn "There seems no Medication Timer instance need to be stop."
		}
		
	}
	
}
