package cc.sukazyo.cono.morny.core.http.api

trait MornyHttpServerContext {
	
	infix def register4API (service: HttpService4Api): Unit
	
	def register4API (service: HttpService4Api*): Unit
	
	def start: HttpServer
	
}
