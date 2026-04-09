package cc.sukazyo.cono.morny.core

sealed trait MornyLifecycleStatus

object MornyLifecycleStatus {
	
	case class Starting (initThread: Thread) extends MornyLifecycleStatus
	
	case class Running () extends MornyLifecycleStatus
	
	case class Stopping () extends MornyLifecycleStatus
	
}
