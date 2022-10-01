package cc.sukazyo.cono.morny.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static cc.sukazyo.cono.morny.util.CommonConvert.byteArrayToHex;
import static cc.sukazyo.cono.morny.util.CommonEncrypt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCommonEncrypt {
	
	@ParameterizedTest
	@SuppressWarnings("UnnecessaryStringEscape")
	@CsvSource(textBlock = """
			28be57d368b75051da76c068a6733284, '莲子'
			9644c5cbae223013228cd528817ba4f5, '莲子\n'
			d41d8cd98f00b204e9800998ecf8427e, ''
			""")
	void testHashMd5_String (String md5, String text) {
		assertEquals(md5, byteArrayToHex(hashMd5(text)));
	}
	
}
