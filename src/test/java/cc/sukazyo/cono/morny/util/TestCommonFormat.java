package cc.sukazyo.cono.morny.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static cc.sukazyo.cono.morny.util.CommonFormat.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommonFormat {
	
	@ParameterizedTest
	@CsvSource(textBlock = """
			1664646870402, 8, 2022-10-02 01:54:30:402
			1, 8, 1970-01-01 08:00:00:001
			0, -1, 1969-12-31 23:00:00:000
			"""
	)
	void testFormatDate (long timestamp, int utfOffset, String expectedHumanReadableTime) {
		assertEquals(expectedHumanReadableTime, formatDate(timestamp, utfOffset));
	}
	
	@ParameterizedTest
	@CsvSource(textBlock = """
			100, '100ms'
			3000, '3s 0ms'
			326117522, '3d 18h 35min 17s 522ms'
			53373805, 14h 49min 33s 805ms
			""")
//			-1, '-1ms'					// WARN: maybe sometime an unexpected usage
//			-194271974291, '-291ms'		//
//			""")						//
	void testFormatDuration (long durationMillis, String humanReadableDuration) {
		assertEquals(humanReadableDuration, formatDuration(durationMillis));
	}
	
}
