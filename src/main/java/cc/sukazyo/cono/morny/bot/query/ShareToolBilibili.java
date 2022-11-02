package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import cc.sukazyo.cono.morny.util.BiliTool;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static cc.sukazyo.cono.morny.Log.logger;
import static cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds;

public class ShareToolBilibili implements ITelegramQuery {
	
	public static final String TITLE_BILI_AV = "[bilibili] Share video / av";
	public static final String TITLE_BILI_BV = "[bilibili] Share video / BV";
	public static final String ID_PREFIX_BILI_AV = "[morny/share/bili/av]";
	public static final String ID_PREFIX_BILI_BV = "[morny/share/bili/bv]";
	public static final Pattern REGEX_BILI_VIDEO = Pattern.compile("^(?:(?:https?://)?(?:www\\.)?bilibili\\.com(?:/s)?/video/((?:av|AV)(\\d+)|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]+))/?(\\?(?:p=(\\d+))?.*)?|(?:av|AV)(\\d+)|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]+))$");
	
	private static final String SHARE_FORMAT_HTML = "<a href='%s'>%s</a>";
	
	@Nullable
	@Override
	public List<InlineQueryUnit<?>> query (Update event) {
		if (event.inlineQuery().query() == null) return null;
		final Matcher regex = REGEX_BILI_VIDEO.matcher(event.inlineQuery().query());
		if (regex.matches()) {
			
//			logger.debug(String.format(
//					"====== ok\n1: %s\n2: %s\n3: %s\n4: %s\n5: %s\n6: %s\n7: %s",
//					regex.group(1), regex.group(2), regex.group(3), regex.group(4),
//					regex.group(5), regex.group(6), regex.group(7)
//			));
			
			// get video id from input, also get video part id
			String av = regex.group(2)==null ? regex.group(6)==null ? null : regex.group(6) : regex.group(2);
			String bv = regex.group(3)==null ? regex.group(7)==null ? null : regex.group(7) : regex.group(3);
//			logger.trace(String.format("catch id av[%s] bv[%s]", av, bv));
			final int part = regex.group(5)==null ? -1 : Integer.parseInt(regex.group(5));
//			logger.trace(String.format("catch part [%s]", part));
			if (av == null) {
				assert bv != null;
				av = String.valueOf(BiliTool.toAv(bv));
//				logger.trace(String.format("converted bv[%s] to av[%s]", bv, av));
			} else {
				bv = BiliTool.toBv(Long.parseLong(av));
//				logger.trace(String.format("converted av[%s] to bv[%s]", av, bv));
			}
			// build standard share links
			final String linkPartParam = part==-1 ? "" : "?p="+part;
			final String linkAv = "https://www.bilibili.com/video/av"+av + linkPartParam;
			final String linkBv = "https://www.bilibili.com/video/BV"+bv + linkPartParam;
			final String idAv = "av"+av;
			final String idBv = "BV"+bv;
//			logger.trace("built all data.");
			
			// build share message element
			List<InlineQueryUnit<?>> result = new ArrayList<>();
			result.add(new InlineQueryUnit<>(new InlineQueryResultArticle(
					inlineIds(ID_PREFIX_BILI_AV+av), TITLE_BILI_AV+av,
					new InputTextMessageContent(String.format(SHARE_FORMAT_HTML, linkAv, idAv)).parseMode(ParseMode.HTML)
			)));
			result.add(new InlineQueryUnit<>(new InlineQueryResultArticle(
					inlineIds(ID_PREFIX_BILI_BV+bv), TITLE_BILI_BV+bv,
					new InputTextMessageContent(String.format(SHARE_FORMAT_HTML, linkBv, idBv)).parseMode(ParseMode.HTML)
			)));
			return result;
			
		}
		return null;
	}
	
}
