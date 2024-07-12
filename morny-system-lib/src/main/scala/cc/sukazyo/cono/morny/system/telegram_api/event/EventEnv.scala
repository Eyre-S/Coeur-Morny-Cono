package cc.sukazyo.cono.morny.system.telegram_api.event

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochMillis
import cc.sukazyo.cono.morny.system.utils.GivenContext
import cc.sukazyo.messiva.utils.StackUtils
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable

/** A Telegram event context that holds status of the event.
  *
  * @param update Associated raw [[Update]] object.
  */
class EventEnv (
	
	val update: Update
	
) {
	
	/** This status contains a [[StackTraceElement]] that shows where the status is set. */
	trait StateSource (val from: StackTraceElement)
	/** Available status of the event. */
	enum State:
		/** The event is successfully processed by someone event listener. */
		case OK (_from: StackTraceElement) extends State with StateSource(_from)
		/** The event is canceled for some reason, it is recommended be ignored. */
		case CANCELED (_from: StackTraceElement) extends State with StateSource(_from)
	
	private val _status: mutable.ListBuffer[State] = mutable.ListBuffer.empty
	/** [[GivenContext Given Contexts]] associated to the event. Can be used to store and share
	  * data between event listeners.
	  * @since 1.3.0
	  */
	val givenCxt: GivenContext = GivenContext()
	/** The [[EpochMillis]] time that bot received this event and preparing to process it.
	  * @since 1.3.0
	  */
	val timeStartup: EpochMillis = System.currentTimeMillis
	
	/** If this event is processed.
	  *
	  * Not only [[State.OK]] but also [[State.CANCELED]] will been seen as processed.
	  *
	  * @since 1.2.0
	  *
	  * @return `true` if the event have been processed, `false` otherwise.
	  */
	def isEventOk: Boolean = _status.lastOption match
		case Some(x) if x == State.OK => true
		case _ => false
	
	/** Set the event status to [[State.OK]].
	  *
	  * This will push a new [[State.OK]] to the status list.
	  */
	//noinspection UnitMethodIsParameterless
	def setEventOk: Unit =
		_status += State.OK(StackUtils.getStackTrace(1).head)
	
	/** Set the event status to [[State.CANCELED]].
	  *
	  * This will push a new [[State.CANCELED]] to the status list.
	  *
	  * @since 1.3.0
	  */
	//noinspection UnitMethodIsParameterless
	def setEventCanceled: Unit =
		_status += State.CANCELED(StackUtils.getStackTrace(1).head)
	
	/** Get the last [[State]] set of the event.
	  * @since 1.3.0
	  */
	def state: State|Null =
		_status.lastOption match
			case Some(x) => x
			case None => null
	
	/** Get all the status set of the event. The earlier status set is in the left.
	  * @since 1.3.0
	  */
	def status: List[State] =
		_status.toList
	
}
