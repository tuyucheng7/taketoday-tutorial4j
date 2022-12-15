package cn.tuyucheng.taketoday.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatePatternUnitTest {

	@Test
	void givenNewPackage_whenPackageReceived_thenStateReceived() {
		Package pkg = new Package();

		assertTrue(pkg.getState() instanceof OrderedState);
		pkg.nextState();

		assertTrue(pkg.getState() instanceof DeliveredState);
		pkg.nextState();

		assertTrue(pkg.getState() instanceof ReceivedState);
	}

	@Test
	void givenDeliveredPackage_whenPrevState_thenStateOrdered() {
		Package pkg = new Package();
		pkg.setState(new DeliveredState());
		pkg.previousState();

		assertTrue(pkg.getState() instanceof OrderedState);
	}
}