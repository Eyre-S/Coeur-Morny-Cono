package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.model.request.InlineQueryResult;

public class InlineQueryUnit<T extends InlineQueryResult<T>> {
	
	public static final int DEFAULT_INLINE_CACHE_TIME = 300;
	public static final boolean DEFAULT_INLINE_PERSONAL_RESP = false;
	
	private int cacheTime = DEFAULT_INLINE_CACHE_TIME;
	private boolean isPersonal = DEFAULT_INLINE_PERSONAL_RESP;
	public final T result;
	
	public InlineQueryUnit (T result) {
		this.result = result;
	}
	
	public int cacheTime () {
		return cacheTime;
	}
	
	public InlineQueryUnit<T> cacheTime (int cacheTime) {
		this.cacheTime = cacheTime;
		return this;
	}
	
	public boolean isPersonal () {
		return isPersonal;
	}
	
	public InlineQueryUnit<T> isPersonal (boolean isPersonal) {
		this.isPersonal = isPersonal;
		return this;
	}
	
}
