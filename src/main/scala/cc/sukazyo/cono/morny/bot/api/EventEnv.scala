package cc.sukazyo.cono.morny.bot.api

import com.pengrad.telegrambot.model.Update

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.boundary

class EventEnv (
	
	val update: Update
	
) {
	
	private var _isOk: Int = 0
	private val variables: mutable.HashMap[Class[?], Any] = mutable.HashMap.empty
	
	def isEventOk: Boolean = _isOk > 0
	
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		_isOk =  _isOk + 1
	
	def provide (i: Any): Unit =
		variables += (i.getClass -> i)
	
	def use [T] (t: Class[T]): ConsumeProvider[T] = ConsumeProvider(t)
	
	class ConsumeProvider[T] (t: Class[T]) {
		def consume (consumer: T => Unit): ConsumeResult = {
			variables get t match
				case Some(i) => consumer(i.asInstanceOf[T]); ConsumeResult(true)
				case None => ConsumeResult(false)
		}
	}
	
	class ConsumeResult (success: Boolean) {
		def onfail (processor: => Unit): Unit = {
			if !success then processor
		}
	}
	
}
