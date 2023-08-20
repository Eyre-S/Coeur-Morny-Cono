package cc.sukazyo.cono.morny.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static cc.sukazyo.cono.morny.util.CommonConvert.byteArrayToHex;
import static cc.sukazyo.cono.morny.util.CommonConvert.byteToHex;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
	
	public enum TestByteArrayToHexSource {
		$1(new T(new byte[]{0x00}, "00")),
		$2(new T(new byte[]{(byte)0xff}, "ff")),
		$3(new T(new byte[]{(byte)0xc3}, "c3")),
		$4(new T(new byte[]{}, "")),
		$5(new T(new byte[]{0x30,0x0a,0x00,0x04,(byte)0xb0,0x00}, "300a0004b000")),
		$6(new T(new byte[]{0x00,0x00,0x0a,(byte)0xff,(byte)0xfc,(byte)0xab,(byte)0x00,0x04}, "00000afffcab0004")),
		$7(new T(new byte[]{0x00,0x7c,0x11,0x28,(byte)0x88,(byte)0xa6,(byte)0xfc,0x30}, "007c112888a6fc30"));
		public record T (byte[] raw, String expected) {}
		public final T value;
		TestByteArrayToHexSource (T value) { this.value = value; }
	}
	@ParameterizedTest
	@EnumSource
	void testByteArrayToHex (TestByteArrayToHexSource source) {
		assertEquals(source.value.expected, byteArrayToHex(source.value.raw));
	}
	
}
