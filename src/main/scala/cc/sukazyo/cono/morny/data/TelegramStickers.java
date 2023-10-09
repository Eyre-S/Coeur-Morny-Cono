package cc.sukazyo.cono.morny.data;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

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
	public static final String ID_501 = "CAACAgEAAxkBAAIHbGUhJ8zm2Sb_c0YU-DYQ6xb-ZDtaAAKdJwACePzGBTOftDZL6X7vMAQ";
	
	@Nonnull
	public static Map<String, String> map () {
		final LinkedHashMap<String, String> mapping = new LinkedHashMap<>();
		for (Field object : TelegramStickers.class.getFields()) {
			if (object.getType()==String.class && object.getName().startsWith("ID_")) {
				try {
					mapping.put(object.getName(), (String)object.get(""));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return mapping;
	}
	
	@Nonnull
	public static Map.Entry<String, String> getById (@Nonnull String stickerFieldID)
	throws NoSuchFieldException {
		try {
			// normally get the sticker and echo
			Field field = TelegramStickers.class.getField(stickerFieldID);
			return Map.entry(field.getName(), (String)field.get(""));
		} catch (IllegalAccessException e) {
			// java-reflect get sticker FILE_ID failed
			throw new RuntimeException(e);
		}
	}
	
}
