package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.CommonConvert;
import cc.sukazyo.cono.morny.util.CommonEncrypt;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Base64;

import static cc.sukazyo.cono.morny.Log.logger;

public class Encryptor implements ITelegramCommand {
	
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
					<b><u>base64url</u></b>, base64u, b64u
					<b><u>base64decode</u></b>, base64d, b64d
					<b><u>base64url-decode</u></b>, base64ud, b64ud
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
		
		// for now, only support reply to A TEXT MESSAGE or ONE UNIVERSAL FILE
		// if the replied message contains a UNIVERSAL FILE, it will use the file and will not use the text with it
		// do not support TELEGRAM INLINE IMAGE/VIDEO/AUDIO yet
		// do not support MULTI_FILE yet
		// if there's no text message in reply, it will report null as result.
		boolean inputText;
		byte[] data;
		String dataName;
		if (event.message().replyToMessage() != null && event.message().replyToMessage().document() != null) {
			inputText = false;
			try {
				data = MornyCoeur.getAccount().getFileContent(MornyCoeur.extra().exec(new GetFile(
						event.message().replyToMessage().document().fileId()
				)).file());
			} catch (IOException e) {
				logger.warn("NetworkRequest error: TelegramFileAPI:\n\t" + e.getMessage());
				MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(),
						TelegramStickers.ID_NETWORK_ERR
				).replyToMessageId(event.message().messageId()));
				return;
			}
			dataName = event.message().replyToMessage().document().fileName();
		} else if (event.message().replyToMessage() != null && event.message().replyToMessage().photo() != null) {
			inputText = false;
			try {
				PhotoSize originPhoto = null;
				long photoSize = 0;
				for (PhotoSize size : event.message().replyToMessage().photo()) if (photoSize < (long)size.width() *size.height()) {
					originPhoto = size;
					photoSize = (long)size.width() *size.height();
				} // found max size (original) image in available sizes
				if (originPhoto==null) throw new IOException("no photo object from api.");
				data = MornyCoeur.getAccount().getFileContent(MornyCoeur.extra().exec(new GetFile(
						originPhoto.fileId()
				)).file());
			} catch (IOException e) {
				logger.warn("NetworkRequest error: TelegramFileAPI:\n\t" + e.getMessage());
				MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(),
						TelegramStickers.ID_NETWORK_ERR
				).replyToMessageId(event.message().messageId()));
				return;
			}
			dataName = "photo"+CommonConvert.byteArrayToHex(CommonEncrypt.hashMd5(String.valueOf(System.currentTimeMillis()))).substring(32-12).toUpperCase()+".png";
		} else if (event.message().replyToMessage() != null && event.message().replyToMessage().text() != null) {
			inputText = true;
			data = event.message().replyToMessage().text().getBytes(CommonEncrypt.ENCRYPT_STANDARD_CHARSET);
			dataName = null;
		} else {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"<i><u>null</u></i>"
			).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
			return;
		}
		
		boolean echoString = true;
		String resultString = null;
		byte[] result = null;
		String resultName = null;
		switch (command.getArgs()[0]) {
			case "base64", "b64", "base64url", "base64u", "b64u" -> {
				final Base64.Encoder b64tool = command.getArgs()[0].contains("u") ? Base64.getUrlEncoder() : Base64.getEncoder();
				result = b64tool.encode(data);
				if (!inputText) {
					echoString = false;
					resultName = dataName+".b64.txt";
				} else {
					resultString = new String(result, CommonEncrypt.ENCRYPT_STANDARD_CHARSET);
				}
			}
			case "base64decode", "base64d", "b64d", "base64url-decode", "base64ud", "b64ud" -> {
				final Base64.Decoder b64tool = command.getArgs()[0].contains("u") ? Base64.getUrlDecoder() : Base64.getDecoder();
				try { result = b64tool.decode(data); }
				catch (IllegalArgumentException e) {
					MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(), TelegramStickers.ID_404
					).replyToMessageId(event.message().messageId()));
					return;
				}
				if (!inputText) {
					echoString = false;
					resultName = CommonEncrypt.base64FilenameLint(dataName);
				} else {
					resultString = new String(result, CommonEncrypt.ENCRYPT_STANDARD_CHARSET);
				}
			}
			case "md5" -> resultString = CommonConvert.byteArrayToHex(CommonEncrypt.hashMd5(data));
			case "sha1" -> resultString = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha1(data));
			case "sha256" -> resultString = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha256(data));
			case "sha512" -> resultString = CommonConvert.byteArrayToHex(CommonEncrypt.hashSha512(data));
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
				case "md5", "sha1", "sha256", "sha512" -> {
					assert resultString != null;
					resultString = resultString.toUpperCase();
				}
				default -> {
					MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(), TelegramStickers.ID_404
					).replyToMessageId(event.message().messageId()));
					return;
				}
			}
		}
		if (echoString) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"<pre><code>" + MsgEscape.escapeHtml(resultString) + "</code></pre>"
			).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		} else {
			MornyCoeur.extra().exec(new SendDocument(
					event.message().chat().id(),
					result
			).fileName(resultName).replyToMessageId(event.message().messageId()));
		}
		
	}
	
}
