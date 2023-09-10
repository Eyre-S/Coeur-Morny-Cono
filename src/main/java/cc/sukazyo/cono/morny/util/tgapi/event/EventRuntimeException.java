package cc.sukazyo.cono.morny.util.tgapi.event;

import com.pengrad.telegrambot.response.BaseResponse;

public class EventRuntimeException extends RuntimeException {
	
	public EventRuntimeException () {
		super();
	}
	
	public EventRuntimeException (String message) {
		super(message);
	}
	
	public static class ActionFailed extends EventRuntimeException {
		
		private final BaseResponse response;
		
		public ActionFailed (BaseResponse response) {
			super();
			this.response = response;
		}
		
		public ActionFailed (String message, BaseResponse response) {
			super(message);
			this.response = response;
		}
		
		public BaseResponse getResponse() {
			return response;
		}
		
	}
	
}
