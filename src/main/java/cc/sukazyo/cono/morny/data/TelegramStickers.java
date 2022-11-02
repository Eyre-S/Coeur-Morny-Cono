package cc.sukazyo.cono.morny.data;

import cc.sukazyo.cono.morny.util.tgapi.ExtraAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.SendResponse;

import java.lang.reflect.Field;

/**
 * 存放 bot 使用到的贴纸
 * @since 0.4.2.0
 */
public class TelegramStickers {
	
	public static final String ID_ONLINE_STATUS_RETURN = "CAACAgEAAx0CW-CvvgAC5eBhhhODGRuu0pxKLwoQ3yMsowjviAACcycAAnj8xgVVU666si1utiIE";
	public static final String ID_HELLO = "CAACAgEAAxkBAAMnYYYWKNXO4ibo9dlsmDctHhhV6fIAAqooAAJ4_MYFJJhrHS74xUAiBA";
	public static final String ID_EXIT = "CAACAgEAAxkBAAMoYYYWt8UjvP0N405SAyvg2SQZmokAAkMiAAJ4_MYFw6yZLu06b-MiBA";
	public static final String ID_403 = "CAACAgEAAxkBAAMqYYYa_7hpXH6hMOYMX4Nh8AVYd74AAnQnAAJ4_MYFRdmmsQKLDZgiBA";
	public static final String ID_404 = "CAACAgEAAx0CSQh32gABA966YbRJpbmi2lCHINBDuo1DknSTsbsAAqUoAAJ4_MYFUa8SIaZriAojBA";
	public static final String ID_WAITING = "CAACAgEAAx0CSQh32gABA-8DYbh7W2VhJ490ucfZMUMrgMR2FW4AAm4nAAJ4_MYFjx6zpxJPWsQjBA";
	public static final String ID_SENT = "CAACAgEAAx0CSQh32gABA--zYbiyU_wOijEitp-0tSl_k7W6l3gAAgMmAAJ4_MYF4GrompjXPx4jBA";
	public static final String ID_SAVED = "CAACAgEAAx0CSQh32gABBExuYdB_G0srfhQldRWkBYxWzCOv4-IAApooAAJ4_MYFcjuNZszfQcQjBA";
	public static final String ID_PROGYNOVA = "CAACAgUAAxkBAAICm2KEuL7UQqNP7vSPCg2DHJIND6UsAAKLAwACH4WSBszIo722aQ3jJAQ";
	public static final String ID_NETWORK_ERR = "CAACAgEAAxkBAAID0WNJgNEkD726KW4vZeFlw0FlVVyNAAIXJgACePzGBb50o7O1RbxoKgQ";
	
	public static void echoAllStickers (ExtraAction actionObject, long sentChat, int replyToMessageId) {
		
		for (Field object : TelegramStickers.class.getFields()) {
			if (object.getType()==String.class && object.getName().startsWith("ID_")) {
				try {
					
					final String stickerId = (String)object.get("");
					SendSticker echo = new SendSticker(sentChat, stickerId);
					SendMessage echoName = new SendMessage(sentChat, object.getName());
					if (replyToMessageId!=-1) echo.replyToMessageId(replyToMessageId);
					SendResponse echoedName = actionObject.exec(echoName);
					actionObject.exec(echo.replyToMessageId(echoedName.message().messageId()));
					
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	public static void echoStickerByID (String stickerFieldID, ExtraAction actionObject, long sentChat, int replyToMessageId) {
		try {
			// normally get the sticker and echo
			Field sticker = TelegramStickers.class.getField(stickerFieldID);
			SendMessage echoName = new SendMessage(sentChat, sticker.getName());
			SendSticker echo = new SendSticker(sentChat, (String)sticker.get(""));
			if (replyToMessageId!=-1) echo.replyToMessageId(replyToMessageId);
			SendResponse echoedName = actionObject.exec(echoName);
			actionObject.exec(echo.replyToMessageId(echoedName.message().messageId()));
		} catch (NoSuchFieldException e) {
			// no such sticker found
			SendSticker echo404 = new SendSticker(sentChat, TelegramStickers.ID_404);
			if (replyToMessageId!=-1) echo404.replyToMessageId(replyToMessageId);
			actionObject.exec(echo404);
		} catch (IllegalAccessException e) {
			// java-reflect get sticker FILE_ID failed
			throw new RuntimeException(e);
		}
	}
	
}
