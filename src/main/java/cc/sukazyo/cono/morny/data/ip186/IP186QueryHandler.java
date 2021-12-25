package cc.sukazyo.cono.morny.data.ip186;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import java.io.IOException;

public class IP186QueryHandler {
	
	public static final String SITE_URL = "https://ip.186526.xyz/";
	
	private static final String QUERY_IP_PARAM = "type=json&format=true";
	private static final String QUERY_WHOIS_PARAM = "type=plain";
	
	private static final OkHttpClient httpClient = new OkHttpClient();
	
	public static IP186QueryResponse queryIp (String ip) throws IOException {
		final String requestUrl = SITE_URL + ip;
		return commonQuery(requestUrl, QUERY_IP_PARAM);
	}
	
	public static IP186QueryResponse queryWhois (String domain) throws IOException {
		final String requestUrl = SITE_URL + "whois/" + domain;
		return commonQuery(requestUrl, QUERY_WHOIS_PARAM);
	}
	
	@Nonnull
	private static IP186QueryResponse commonQuery (String requestUrl, String queryIpParam) throws IOException {
		Request request = new Request.Builder().url(requestUrl + "?" + queryIpParam).build();
		try (Response response = httpClient.newCall(request).execute()) {
			final ResponseBody body = response.body();
			if (body == null) throw new IOException("Null body.");
			return new IP186QueryResponse(requestUrl, body.string());
		}
	}
	
}
