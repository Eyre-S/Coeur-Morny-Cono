package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.CommonConvert;
import cc.sukazyo.cono.morny.util.CommonEncrypt;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Base64;

public class EncUtils implements ITelegramCommand {
	
	@Nonnull @Override public String getName () { return "encrypt"; }
	@Nullable @Override public String[] getAliases () { return new String[0]; }
	@Nonnull @Override public String getParamRule () { return "[algorithm|(l)] [(uppercase)]"; }
	@Nonnull @Override public String getDescription () { return "通过指定算法加密回复的内容 (目前只支持文本)"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		// show a simple help page
		// the first paragraph lists available encrypt algorithms, and its aliases.
		// with the separator "---",
		// the second paragraphs shows the mods available and its aliases.
		if (!command.hasArgs() || (command.getArgs()[0].equals("l") && command.getArgs().length==1)) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(), """
					<b><u>base64</u></b>, b64
					<b><u>base64decode</u></b>, base64d, b64d
					<b><u>sha1</u></b>
					<b><u>sha256</u></b>
					<b><u>sha512</u></b>
					<b><u>md5</u></b>
					---
					<b><i>uppercase</i></b>, upper, u <i>(sha1/sha256/sha512/md5 only)</i>
					"""
			).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
			return;
		}
		
		// param1 is the encrypting algorithm, it MUST EXIST.
		// so the mod will be set in param2.
		// and for now only support UPPERCASE mod, so it exists in param2, or there should no any params.
		boolean modUpperCase = false;
		if (command.getArgs().length > 1) {
			if (command.getArgs().length < 3 && (
					command.getArgs()[1].equalsIgnoreCase("uppercase") ||
					command.getArgs()[1].equalsIgnoreCase("u") ||
					command.getArgs()[1].equalsIgnoreCase("upper")
			)) {
				modUpperCase = true;
			} else {
				MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(), TelegramStickers.ID_404
				).replyToMessageId(event.message().messageId()));
				return;
			}
		}
		
		// for now, only support reply to A TEXT MESSAGE and encrypt/hash the text value.
		// if there's no text message in reply, it will report null as result.
		if (event.message().replyToMessage() == null || event.message().replyToMessage().text() == null) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"<i><u>null</u></i>"
			).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
			return;
		}
		final String data = event.message().replyToMessage().text();
		
		String result;
		switch (command.getArgs()[0]) {
			case "base64", "b64" -> result = Base64.getEncoder().encodeToString(data.getBytes(CommonEncrypt.ENCRYPT_STANDARD_CHARSET));
			case "base64decode", "base64d", "b64d" -> result = new String(
					Base64.getDecoder().decode(data.getBytes(CommonEncrypt.ENCRYPT_STANDARD_CHARSET)), CommonEncrypt.ENCRYPT_STANDARD_CHARSET);
			case "md5" -> result = CommonConvert.byteArrayToHex(CommonEncrypt.hashMd5(data));
			case "sha1" -> result = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha1(data));
			case "sha256" -> result = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha256(data));
			case "sha512" -> result = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha512(data));
			default -> {
				MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(), TelegramStickers.ID_404
				).replyToMessageId(event.message().messageId()));
				return;
			}
		}
		if (modUpperCase) {
			// modUpperCase support only algorithm that showed as HEX value.
			// it means md5, sha1, sha256, sha512 here.
			// other will report wrong param.
			switch (command.getArgs()[0]) {
				case "md5", "sha1", "sha256", "sha512" ->
						result = result.toUpperCase();
				default -> {
					MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(), TelegramStickers.ID_404
					).replyToMessageId(event.message().messageId()));
					return;
				}
			}
		}
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				"<pre><code>" + MsgEscape.escapeHtml(result) + "</code></pre>"
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		
	}
	
}
