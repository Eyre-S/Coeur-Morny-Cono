package cc.sukazyo.cono.morny.daemon;

import cc.sukazyo.cono.morny.Log;
import cc.sukazyo.cono.morny.MornyCoeur;
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

import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;

public class MornyReport {
	
	private static <T extends BaseRequest<T, R>, R extends BaseResponse> void executeReport (@Nonnull T report) {
		if (!MornyCoeur.available()) return;
		try {
			MornyCoeur.extra().exec(report);
		} catch (EventRuntimeException.ActionFailed e) {
			Log.logger.warn("cannot execute report to telegram:");
			Log.logger.warn(Log.exceptionLog(e));
		}
	}
	
	public static void exception (@Nonnull Exception e, @Nullable String description) {
		if (!MornyCoeur.available()) return;
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
		if (!MornyCoeur.available()) return;
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
	
}
