package cc.sukazyo.cono.morny.util.hytrans

//opaque type Definitions = Map[String, String]

class Definitions (
	innerData: Map[String, String]
) extends Map[String, String] {
	
	def iterator: Iterator[(String, String)] = innerData.iterator
	def removed (key: String): Map[String, String] = innerData.removed(key)
	def updated[V1 >: String] (key: String, value: V1): Map[String, V1] = innerData.updated(key, value)
	def get (key: String): Option[String] = innerData.get(key)
	
}
