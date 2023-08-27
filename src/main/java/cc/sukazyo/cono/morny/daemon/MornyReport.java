package cc.sukazyo.cono.morny.daemon;

import cc.sukazyo.cono.morny.*;
import cc.sukazyo.cono.morny.bot.command.MornyInformation;
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException;
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.lang.reflect.Field;

import static cc.sukazyo.cono.morny.Log.logger;
import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;

public class MornyReport {
	
	private static boolean unsupported () {
		return !MornyCoeur.available() || MornyCoeur.config().reportToChat == -1;
	}
	
	private static <T extends BaseRequest<T, R>, R extends BaseResponse> void executeReport (@Nonnull T report) {
		if (unsupported()) return;
		try {
			MornyCoeur.extra().exec(report);
		} catch (EventRuntimeException.ActionFailed e) {
			logger.warn("cannot execute report to telegram:");
			logger.warn(Log.exceptionLog(e).indent(4));
			logger.warn("tg-api response:");
			logger.warn(e.getResponse().toString().indent(4));
		}
	}
	
	public static void exception (@Nonnull Exception e, @Nullable String description) {
		if (unsupported()) return;
		executeReport(new SendMessage(
				MornyCoeur.config().reportToChat,
				String.format("""
						<b>▌Coeur Unexpected Exception</b>
						%s
						<pre><code>%s</code></pre>%s
						""",
						description == null ? "" : escapeHtml(description)+"\n",
						escapeHtml(Log.exceptionLog(e)),
						e instanceof EventRuntimeException.ActionFailed ? (String.format(
								"\n\ntg-api error:\n<pre><code>%s</code></pre>",
								new GsonBuilder().setPrettyPrinting().create().toJson(((EventRuntimeException.ActionFailed)e).getResponse()))
						) : ""
				)
		).parseMode(ParseMode.HTML));
	}
	
	public static void exception (@Nonnull Exception e) { exception(e, null); }
	
	public static void unauthenticatedAction (@Nonnull String action, @Nonnull User user) {
		if (unsupported()) return;
		executeReport(new SendMessage(
				MornyCoeur.config().reportToChat,
				String.format("""
						<b>▌User unauthenticated action</b>
						action: %s
						by user %s
						""",
						escapeHtml(action),
						TGToString.as(user).fullnameRefHtml()
				)
		).parseMode(ParseMode.HTML));
	}
	
	/**
	 * morny 登陆时的报告发送，包含已登录的账号 id 以及启动配置。
	 * @since 1.0.0-alpha6
	 */
	static void onMornyLogIn () {
		executeReport(new SendMessage(
				MornyCoeur.config().reportToChat,
				String.format("""
						<b>▌Morny Logged in</b>
						-v %s
						as user @%s
						
						as config fields:
						%s
						""",
						MornyInformation.getVersionAllFullTagHtml(),
						MornyCoeur.getUsername(),
						sectionConfigFields(MornyCoeur.config())
				)
		).parseMode(ParseMode.HTML));
	}
	
	/**
	 * 返回一个 config 字段与值的列表，可以作为 telegram html 格式输出
	 * @since 1.0.0-alpha6
	 */
	private static String sectionConfigFields (@Nonnull MornyConfig config) {
		final StringBuilder echo = new StringBuilder();
		for (Field field : config.getClass().getFields()) {
			echo.append("- <i><u>").append(field.getName()).append("</u></i> ");
			try {
				if (field.isAnnotationPresent(MornyConfig.Sensitive.class)) {
					echo.append(": <i>sensitive_field</i>");
				} else {
					final Object fieldValue = field.get(config);
					echo.append("= ");
					if (fieldValue == null)
						echo.append("null");
					else echo.append("<code>").append(escapeHtml(fieldValue.toString())).append("</code>");
				}
			} catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
				echo.append(": <i>").append(escapeHtml("<read-error>")).append("</i>");
				logger.error("error while reading config field " + field.getName());
				logger.error(Log.exceptionLog(e));
				exception(e, "error while reading config field " + field.getName());
			}
			echo.append("\n");
		}
		return echo.substring(0, echo.length()-1);
	}
	
	/**
	 * morny 关闭/登出时发送的报告.
	 * <p>
	 * 基于 java 的程序关闭钩子，因此仍然无法在意外宕机的情况下发送报告.
	 * @param causedBy
	 *        关闭的原因。
	 *        可以使用 {@link User Telegram 用户对象} 表示由一个用户执行了关闭，
	 *        传入其它数据将使用 {@code #toString} 输出其内容。
	 *        传入 {@link null} 则表示不表明原因。
	 */
	static void onMornyExit (@Nullable Object causedBy) {
		if (unsupported()) return;
		String causedTag = null;
		if (causedBy != null) {
			if (causedBy instanceof User)
				causedTag = TGToString.as((User)causedBy).fullnameRefHtml();
			else
				causedTag = "<code>" + escapeHtml(causedBy.toString()) + "</code>";
		}
		executeReport(new SendMessage(
				MornyCoeur.config().reportToChat,
				String.format("""
						<b>▌Morny Exited</b>
						from user @%s
						%s
						""",
						MornyCoeur.getUsername(),
						causedBy == null ? "with UNKNOWN reason" : "\nby " + causedTag
				)
		).parseMode(ParseMode.HTML));
	}
	
}
