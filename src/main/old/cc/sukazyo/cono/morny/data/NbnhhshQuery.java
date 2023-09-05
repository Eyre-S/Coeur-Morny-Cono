package cc.sukazyo.cono.morny.data;

import java.io.IOException;

import com.google.gson.Gson;

import cc.sukazyo.cono.morny.util.OkHttpPublic.MediaTypes;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NbnhhshQuery {
	
	public static class Word {
		public String name;
		public String[] trans;
		public String[] inputting;
	}
	
	public static class GuessResult {
		public Word[] words;
	}
	
	public record GuessReq (String text) {}
	
	public static final String API_URL = "https://lab.magiconch.com/api/nbnhhsh/";
	public static final String API_GUESS_METHOD = "guess/";
	
	private static final OkHttpClient httpClient = new OkHttpClient();
	
	public static GuessResult sendGuess (String text) throws IOException {
		final String reqJsonText = new Gson().toJson(new GuessReq(text));
		Request request = new Request.Builder()
				.url(API_URL + API_GUESS_METHOD)
				.post(RequestBody.create(reqJsonText, MediaTypes.JSON))
				.build();
		try (Response response = httpClient.newCall(request).execute()) {
			final ResponseBody body = response.body();
			if (body == null) throw new IOException("Null body.");
			final String x = "{ \"words\": " + body.string() + " }";
			return new Gson().fromJson(x, GuessResult.class);
		}
	}
	
}
