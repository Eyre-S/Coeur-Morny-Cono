package cc.sukazyo.cono.morny.extra.bilibili

case class XWebResponse [T] (
	code: Int,
	message: String,
	ttl: Int,
	data: T
)
