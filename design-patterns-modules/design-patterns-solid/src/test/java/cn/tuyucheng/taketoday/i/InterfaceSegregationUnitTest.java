package cn.tuyucheng.taketoday.i;

import org.junit.jupiter.api.Test;

class InterfaceSegregationUnitTest {

	@Test
	void whenUsingIS_thenShouldCalledRelatedMethod() {
		BearCarer bearCarer = new BearCarer();
		bearCarer.feedTheBear();
		bearCarer.washTheBear();

		CrazyPerson crazyPerson = new CrazyPerson();
		crazyPerson.petTheBear();
	}
}