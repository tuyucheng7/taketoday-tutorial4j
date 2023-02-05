package cn.tuyucheng.taketoday.interfaceVsAbstractClass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InterfaceVsAbstractClassUnitTest {

	@Test
	void givenAbstractClass_whenValidCircleUsed_thenPass() {
		CircleClass redCircle = new ChildCircleClass();
		redCircle.setColor("RED");

		assertTrue(redCircle.isValid());
	}

	@Test
	void givenInterface_whenValidCircleWithoutStateUsed_thenPass() {
		ChildCircleInterfaceImpl redCircleWithoutState = new ChildCircleInterfaceImpl();
		redCircleWithoutState.setColor("RED");

		assertTrue(redCircleWithoutState.isValid());
	}
}