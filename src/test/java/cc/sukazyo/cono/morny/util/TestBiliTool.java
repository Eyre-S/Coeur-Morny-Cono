package cc.sukazyo.cono.morny.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static cc.sukazyo.cono.morny.util.BiliTool.*;

public class TestBiliTool {
	
	private static final String AV_BV_DATA_CSV = """
			17x411w7KC, 170001
			1Q541167Qg, 455017605
			1mK4y1C7Bz, 882584971
			1T24y197V2, 688730800
			""";
	
	@ParameterizedTest
	@CsvSource(textBlock = AV_BV_DATA_CSV)
	void testAvToBv (String bv, int av) {
		Assertions.assertEquals(bv, toBv(av));
	}
	
	@ParameterizedTest
	@CsvSource(textBlock = AV_BV_DATA_CSV)
	void testBvToAv (String bv, int av) {
		Assertions.assertEquals(av, toAv(bv));
	}
	
}
