package cc.sukazyo.cono.morny.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static cc.sukazyo.cono.morny.util.CommonConvert.byteArrayToHex;
import static cc.sukazyo.cono.morny.util.CommonConvert.byteToHex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestCommonConvert {
	
	@ParameterizedTest
	@CsvSource(textBlock = """
			0x00, 00
			0x01, 01
			0x20, 20
			0x77, 77
			-0x60, a0
			0x0a, 0a
			-0x01, ff
			-0x05, fb
			"""
	)
	void testByteToHex(byte source, String expected) {
		assertEquals(expected, byteToHex(source));
	}
	
	public static Stream<Arguments> testByteArrayToHexProvider () {
		return Stream.of(
				arguments(new byte[]{0x00}, "00"),
				arguments(new byte[]{(byte)0xff}, "ff"),
				arguments(new byte[]{(byte)0xc3}, "c3"),
				arguments(new byte[]{}, ""),
				arguments(new byte[]{0x30,0x0a,0x00,0x04,(byte)0xb0,0x00}, "300a0004b000"),
				arguments(new byte[]{0x00,0x00,0x0a,(byte)0xff,(byte)0xfc,(byte)0xab,(byte)0x00,0x04}, "00000afffcab0004"),
				arguments(new byte[]{0x00,0x7c,0x11,0x28,(byte)0x88,(byte)0xa6,(byte)0xfc,0x30}, "007c112888a6fc30")
		);
	}
	@ParameterizedTest
	@MethodSource("testByteArrayToHexProvider")
	void testByteArrayToHex (byte[] raw, String expected) {
		assertEquals(expected, byteArrayToHex(raw));
	}
	
}
