package cn.tuyucheng.taketoday.concurrent.threadsafety;

import cn.tuyucheng.taketoday.concurrent.threadsafety.mathutils.MathUtils;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class MathUtilsUnitTest {

	@Test
	void whenCalledFactorialMethod_thenCorrect() {
		assertThat(MathUtils.factorial(2)).isEqualTo(new BigInteger("2"));
	}
}