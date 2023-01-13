package cn.tuyucheng.taketoday.jna;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CMathUnitTest {
	@Test
	void whenCallNative_thenSuccess() {
		CMath lib = Native.load(Platform.isWindows() ? "msvcrt" : "c", CMath.class);
		double result = lib.cosh(0);
		assertEquals(1.0, result);
	}
}