package cn.tuyucheng.taketoday.diamondoperator;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DiamondOperatorUnitTest {
	@Test
	public void whenCreateCarUsingDiamondOperator_thenSuccess() {
		Car<Diesel> myCar = new Car<>();
		assertNotNull(myCar);
	}
}
