package cc.sukazyo.cono.morny.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CommonFormat {
	
	public static final String DATE_TIME_PATTERN_FULL_MILLIS = "yyyy-MM-dd HH:mm:ss:SSS";
	
	public static String formatDate (long timestamp, int utcOffset) {
		return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_FULL_MILLIS).format(LocalDateTime.ofInstant(
				Instant.ofEpochMilli(timestamp),
				ZoneId.ofOffset("UTC", ZoneOffset.ofHours(utcOffset))
		));
	}
	
	public static String formatDuration (long duration) {
		StringBuilder sb = new StringBuilder();
		if (duration > 1000 * 60 * 60 * 24) sb.append(duration / (1000*60*60*24)).append("d ");
		if (duration > 1000 * 60 * 60) sb.append(duration / (1000*60*60) % 24).append("h ");
		if (duration > 1000 * 60) sb.append(duration / (1000*60) % 60).append("min ");
		if (duration > 1000) sb.append(duration / 1000 % 60).append("s ");
		sb.append(duration % 1000).append("ms");
		return sb.toString();
	}
	
}
