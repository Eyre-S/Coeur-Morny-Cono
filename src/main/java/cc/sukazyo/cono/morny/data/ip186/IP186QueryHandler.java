package cc.sukazyo.cono.morny.data.ip186;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * 通过 {@value #SITE_URL} 进行 {@link #queryIp ip}/{@link #queryWhois whois} 数据查询的工具类
 *
 * @since 0.4.2.10
 */
public class IP186QueryHandler {
	
	/**
	 * 请求所使用的 HTTP API 站点链接
	 * @since 0.4.2.10
	 */
	public static final String SITE_URL = "https://ip.186526.xyz/";
	
	/**
	 * 进行 {@link #queryIp ip 查询}时所使用的 API 参数.<br>
	 * 目的使 API 直接返回原始数据
	 */
	private static final String QUERY_IP_PARAM = "type=json&format=true";
	
	/**
	 * 进行 {@link #queryWhois whois 查询}时所使用的 API 参数.<br>
	 * 目的使 API 直接返回原始数据
	 */
	private static final String QUERY_WHOIS_PARAM = "type=plain";
	
	/** 请求时使用的 OkHttp 请求工具实例 */
	private static final OkHttpClient httpClient = new OkHttpClient();
	
	/**
	 * 通过 {@value #SITE_URL} 获取 ip 信息.
	 * @see #QUERY_IP_PARAM  发送请求时所使用的 API 参数
	 * @param ip 需要进行查询的 ip
	 * @return 查询结果。data 根据 {@value #SITE_URL} 的规则以 json 序列化
	 * @throws IOException 任何请求或解析错误
	 */
	@Nonnull
	public static IP186QueryResponse queryIp (String ip) throws IOException {
		final String requestUrl = SITE_URL + ip;
		return commonQuery(requestUrl, QUERY_IP_PARAM);
	}
	
	/**
	 * 通过 {@value #SITE_URL} 获取域名信息.
	 * @see #QUERY_WHOIS_PARAM  发送请求时所使用的 API 参数
	 * @param domain 需要进行查询的域名
	 * @return 查询结果。data 根据 {@value #SITE_URL} 的规则以 plain 序列化
	 * @throws IOException 任何请求或解析错误
	 */
	@Nonnull
	public static IP186QueryResponse queryWhois (String domain) throws IOException {
		final String requestUrl = SITE_URL + "whois/" + domain;
		return commonQuery(requestUrl, QUERY_WHOIS_PARAM);
	}
	
	/**
	 * 将 {@link #queryWhois(String)} 的结果进行裁剪.
	 * <br>
	 * 将会删除返回内容中 {@code >>> XXX <<<} 行以后的注释串，
	 * 以达到只保留重要信息的目的。
	 * 
	 * @see #queryWhois(String)
	 */
	@Nonnull
	public static IP186QueryResponse queryWhoisPretty (String domain) throws IOException {
		final IP186QueryResponse raw = queryWhois(domain);
		return new IP186QueryResponse(raw.url(), raw.body().substring(0, raw.body().indexOf("<<<")+3));
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
