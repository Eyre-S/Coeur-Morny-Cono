package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
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
	private val variables: mutable.HashMap[Class[?], Any] = mutable.HashMap.empty
	val timeStartup: EpochMillis = System.currentTimeMillis
	
	def isEventOk: Boolean = _status.lastOption match
		case Some(x) if x == State.OK => true
		case _ => false
	
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		_status += State.OK(StackUtils.getStackTrace(1)(0))
	
	//noinspection UnitMethodIsParameterless
	def setEventCanceled: Unit =
		_status += State.CANCELED(StackUtils.getStackTrace(1)(0))
	
	def state: State|Null =
		_status.lastOption match
			case Some(x) => x
			case None => null
	
	def status: List[State] =
		_status.toList
	
	def provide (i: Any): Unit =
		variables += (i.getClass -> i)
	
	def consume [T] (t: Class[T]) (consumer: T => Unit): ConsumeResult = {
		variables get t match
			case Some(i) => consumer(i.asInstanceOf[T]); ConsumeResult(true)
			case None => ConsumeResult(false)
	}
	
	// fixme: defined() series is not tested yet
	
	def defined [T: ClassTag]: Boolean = {
		variables get classTag[T].runtimeClass match
			case Some(_) => true
			case None => false
	}
	
	def defined [T] (t: Class[T]): Boolean = {
		variables get t match
			case Some(_) => true
			case None => false
	}
	
	def consume [T: ClassTag] (consumer: T => Unit): ConsumeResult =
		variables get classTag[T].runtimeClass match
			case Some(i) => consumer(i.asInstanceOf[T]); ConsumeResult(true)
			case None => ConsumeResult(false)
	
	class ConsumeResult (success: Boolean) {
		def onfail (processor: => Unit): Unit = {
			if !success then processor
		}
	}
	
}
