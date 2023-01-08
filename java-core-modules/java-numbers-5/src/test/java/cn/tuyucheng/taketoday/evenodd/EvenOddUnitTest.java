package cn.tuyucheng.taketoday.evenodd;

import org.junit.Test;

import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isAndEven;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isAndOdd;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isEven;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isLsbEven;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isLsbOdd;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isOdd;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isOrEven;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isOrOdd;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isXorEven;
import static cn.tuyucheng.taketoday.evenodd.EvenOdd.isXorOdd;
import static org.junit.Assert.assertEquals;

public class EvenOddUnitTest {

	@Test
	public void whenNumberIsEven_thenReturnTrue() {
		assertEquals(true, isEven(2));
	}

	@Test
	public void whenNumberIsOdd_thenReturnTrue() {
		assertEquals(true, isOdd(3));
	}

	@Test
	public void whenNumberIsEven_thenReturnTrueWithOr() {
		assertEquals(true, isOrEven(4));
	}

	@Test
	public void whenNumberIsOdd_thenReturnTrueOr() {
		assertEquals(true, isOrOdd(5));
	}

	@Test
	public void whenNumberIsEven_thenReturnTrueAnd() {
		assertEquals(true, isAndEven(6));
	}

	@Test
	public void whenNumberIsOdd_thenReturnTrueAnd() {
		assertEquals(true, isAndOdd(7));
	}

	@Test
	public void whenNumberIsEven_thenReturnTrueXor() {
		assertEquals(true, isXorEven(8));
	}

	@Test
	public void whenNumberIsOdd_thenReturnTrueXor() {
		assertEquals(true, isXorOdd(9));
	}

	@Test
	public void whenNumberIsEven_thenReturnTrueLsb() {
		assertEquals(true, isLsbEven(10));
	}

	@Test
	public void whenNumberIsOdd_thenReturnTrueLsb() {
		assertEquals(true, isLsbOdd(11));
	}
}
