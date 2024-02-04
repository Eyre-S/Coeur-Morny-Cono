package cc.sukazyo.cono.morny.core.http.api

trait MornyHttpServerContext {
	
	def register4API (service: HttpService4Api*): Unit
	
	def start: HttpServer
	
}
