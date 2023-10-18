package cc.sukazyo.cono.morny.internal.logging

import cc.sukazyo.messiva.log.Message

trait IMornyLogLevelImpl {
	
	def notice (message: String): Unit
	def notice (message: Message): Unit
	def attention (message: String): Unit
	def attention (message: Message): Unit
	
}
