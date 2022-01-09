package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public class Executor {
	
	private final TelegramBot client;
	
	public Executor (TelegramBot bot) {
		client = bot;
	}
	
	public static Executor as (TelegramBot bot) {
		return new Executor(bot);
	}
	
	public <T extends BaseRequest<T, R>, R extends BaseResponse> R exec (T req) {
		return exec(req, "");
	}
	
	public <T extends BaseRequest<T, R>, R extends BaseResponse> R exec (T req, String errorMessage) {
		final R resp = client.execute(req);
		if (!resp.isOk()) throw new EventRuntimeException.ActionFailed(
				(errorMessage.equals("") ? String.valueOf(resp.errorCode()) : errorMessage),
				resp
		);
		return resp;
	}
	
}
