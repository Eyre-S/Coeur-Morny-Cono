package cc.sukazyo.cono.morny.daemon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

public class TestMedicationTimer {
	
	@ParameterizedTest
	@CsvSource(textBlock = """
			2022-11-13T13:14:35+08, +08, 2022-11-14T12:00:00+08
			2022-11-13T13:14:35+02, +02, 2022-11-14T12:00:00+02
			2022-11-13T08:14:35+08, +08, 2022-11-13T12:00:00+08
			2022-11-13T00:14:35+08, +08, 2022-11-13T12:00:00+08
			2022-11-13T12:00:00+00, +00, 2022-11-14T12:00:00+00
			""")
	void testCalcNextRoutineTimestamp (ZonedDateTime base, ZoneOffset zoneHour, ZonedDateTime expected) {
		final Set<Integer> at = Set.of(12);
		Assertions.assertEquals(
				expected.toEpochSecond()*1000,
				MedicationTimer.calcNextRoutineTimestamp(base.toEpochSecond()*1000, zoneHour, at)
		);
	}
	
}
