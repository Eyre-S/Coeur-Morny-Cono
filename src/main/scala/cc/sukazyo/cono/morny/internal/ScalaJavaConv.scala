package cc.sukazyo.cono.morny.internal

import scala.jdk.CollectionConverters._
import scala.collection.immutable as simm
import java.util as j

object ScalaJavaConv {
	
	def jSetInteger2simm (data: j.Set[Integer]): simm.Set[Int] =
		data.asScala.toSet.map(_.intValue)
	
}
