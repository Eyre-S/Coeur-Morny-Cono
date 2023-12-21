package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import cc.sukazyo.cono.morny.util.GivenContext
import cc.sukazyo.messiva.utils.StackUtils
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable
import scala.reflect.{classTag, ClassTag}

class EventEnv (
	
	val update: Update
	
) {
	
	trait StateSource (val from: StackTraceElement)
	enum State:
		case OK (_from: StackTraceElement) extends State with StateSource(_from)
		case CANCELED (_from: StackTraceElement) extends State with StateSource(_from)
	
	private val _status: mutable.ListBuffer[State] = mutable.ListBuffer.empty
	val givenCxt: GivenContext = GivenContext()
	val timeStartup: EpochMillis = System.currentTimeMillis
	
	def isEventOk: Boolean = _status.lastOption match
		case Some(x) if x == State.OK => true
		case _ => false
	
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		_status += State.OK(StackUtils.getStackTrace(1)(1))
	
	//noinspection UnitMethodIsParameterless
	def setEventCanceled: Unit =
		_status += State.CANCELED(StackUtils.getStackTrace(1)(1))
	
	def state: State|Null =
		_status.lastOption match
			case Some(x) => x
			case None => null
	
	def status: List[State] =
		_status.toList
	
}
