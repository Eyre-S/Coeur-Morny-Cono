package cc.sukazyo.cono.morny.util;

import com.pengrad.telegrambot.model.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class TelegramUserInformation {
	
	public static final String DC_QUERY_SOURCE_SITE = "https://t.me/";
	public static final Pattern DC_QUERY_PROCESSOR_REGEX = Pattern.compile("(cdn[1-9]).tele(sco.pe|gram-cdn.org)");
	
	private static final OkHttpClient httpClient = new OkHttpClient();
	
	@Nullable
	public static String getDataCenterFromUsername (String username) {
		final Request request = new Request.Builder().url(DC_QUERY_SOURCE_SITE + username).build();
		try (Response response = httpClient.newCall(request).execute()) {
			final ResponseBody body = response.body();
			if (body == null) return "empty upstream response";
			final Matcher matcher = DC_QUERY_PROCESSOR_REGEX.matcher(body.string());
			if (matcher.find()) {
				return matcher.group(1);
			}
		} catch (IOException e) {
			return e.getMessage();
		}
		return null;
	}
	
	public static String informationOutputHTML (User user) {
		
		final StringBuilder userInformation = new StringBuilder();
		userInformation.append(String.format(
				"""
				userid :
				- <code>%d</code>""",
				user.id()
		));
		if (user.username() == null) {
			userInformation.append("\nusername : <u>null</u>\ndatacenter : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"""
					
					username :
					- <code>%s</code>""",
					escapeHtml(user.username())
			));
			// 依赖 username 的 datacenter 查询
			final String dataCenter = getDataCenterFromUsername(user.username());
			if (dataCenter == null) { userInformation.append("\ndatacenter : <u>null</u>"); }
			else { userInformation.append(String.format("\ndatacenter : <code>%s</code>", escapeHtml(dataCenter))); }
		}
		userInformation.append(String.format(
				"""
				
				display name :
				- <code>%s</code>%s""",
				escapeHtml(user.firstName()),
				user.lastName()==null ? "" : String.format("\n- <code>%s</code>", escapeHtml(user.lastName()))
		));
		if (user.languageCode() != null) {
			userInformation.append(String.format(
					"""
					
					language-code :
					- <code>%s</code>""",
					escapeHtml(user.languageCode())
			));
		}
		
		return userInformation.toString();
		
	}
	
}
