package cc.sukazyo.cono.morny.core

import cc.sukazyo.std.event.EventContext

package object event {
	
	def initWithCoeur (using coeur: MornyCoeur): EventContext[?]=>Unit
	= context => {
		context.givenCxt << coeur
	}
	
}
