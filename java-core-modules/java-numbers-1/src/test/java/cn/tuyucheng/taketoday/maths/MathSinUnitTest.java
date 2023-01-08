package cn.tuyucheng.taketoday.maths;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MathSinUnitTest {

	@Test
	public void givenAnAngleInDegrees_whenUsingToRadians_thenResultIsInRadians() {
		double angleInDegrees = 30;
		double sinForDegrees = Math.sin(Math.toRadians(angleInDegrees)); // 0.5

		double thirtyDegreesInRadians = (double) 1 / 6 * Math.PI;
		double sinForRadians = Math.sin(thirtyDegreesInRadians); // 0.5

		assertThat(sinForDegrees, is(sinForRadians));
	}

}
