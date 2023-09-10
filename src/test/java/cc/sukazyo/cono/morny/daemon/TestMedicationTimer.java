package cc.sukazyo.cono.morny.daemon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

import static cc.sukazyo.cono.morny.internal.ScalaJavaConv.jSetInteger2simm;

public class TestMedicationTimer {
	
	@ParameterizedTest
	@CsvSource(textBlock = """
			2022-11-13T13:14:35.000+08, +08, 2022-11-13T19:00:00+08
			2022-11-13T13:14:35.174+02, +02, 2022-11-13T19:00:00+02
			1998-02-01T08:14:35.871+08, +08, 1998-02-01T19:00:00+08
			2022-11-13T00:00:00.000-01, -01, 2022-11-13T07:00:00-01
			2022-11-21T19:00:00.000+00, +00, 2022-11-21T21:00:00+00
			2022-12-31T21:00:00.000+00, +00, 2023-01-01T07:00:00+00
			2125-11-18T23:45:27.062+00, +00, 2125-11-19T07:00:00+00
			""")
	void testCalcNextRoutineTimestamp (ZonedDateTime base, ZoneOffset zoneHour, ZonedDateTime expected)
	throws IllegalArgumentException {
		final Set<Integer> at = Set.of(7, 19, 21);
		System.out.println("base.toInstant().toEpochMilli() = " + base.toInstant().toEpochMilli());
		Assertions.assertEquals(
				expected.toInstant().toEpochMilli(),
				MedicationTimer.calcNextRoutineTimestamp(base.toInstant().toEpochMilli(), zoneHour, jSetInteger2simm(at))
		);
		System.out.println(" ok");
	}
	
}
