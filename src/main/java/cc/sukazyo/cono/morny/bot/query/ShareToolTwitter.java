package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds;

public class ShareToolTwitter implements ITelegramQuery {
	
	public static final String TITLE_VX = "[tweet] Share as VxTwitter";
	public static final String TITLE_VX_COMBINED = "[tweet] Share as VxTwitter(combination)";
	public static final String ID_PREFIX_VX = "[morny/share/twitter/vxtwi]";
	public static final String ID_PREFIX_VX_COMBINED = "[morny/share/twitter/vxtwi_combine]";
	
	public static final Pattern REGEX_TWEET_LINK = Pattern.compile(
			"^(?:https?://)?((?:(?:c\\.)?vx|fx|www\\.)?twitter\\.com)/((\\w+)/status/(\\d+)(?:/photo/(\\d+))?)/?(\\?[\\w&=-]+)?$");
	
	@Nullable
	@Override
	public List<InlineQueryUnit<?>> query (@Nonnull Update event) {
		if (event.inlineQuery().query() == null) return null;
		final Matcher regex = REGEX_TWEET_LINK.matcher(event.inlineQuery().query());
		if (regex.matches()) {
			
			List<InlineQueryUnit<?>> result = new ArrayList<>();
			
			result.add(new InlineQueryUnit<>(new InlineQueryResultArticle(
					inlineIds(ID_PREFIX_VX+event.inlineQuery().query()), TITLE_VX,
					String.format("https://vxtwitter.com/%s", regex.group(2))
			)));
			result.add(new InlineQueryUnit<>(new InlineQueryResultArticle(
					inlineIds(ID_PREFIX_VX_COMBINED+event.inlineQuery().query()), TITLE_VX_COMBINED,
					String.format("https://c.vxtwitter.com/%s", regex.group(2))
			)));
			
			return result;
			
		}
		return null;
	}
	
}
