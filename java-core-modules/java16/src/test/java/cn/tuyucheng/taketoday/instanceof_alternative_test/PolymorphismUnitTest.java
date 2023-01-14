package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceof_alternatives.model.Anatotitan;
import cn.tuyucheng.taketoday.instanceof_alternatives.model.Dinosaur;
import cn.tuyucheng.taketoday.instanceof_alternatives.model.Euraptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolymorphismUnitTest {

	@Test
	void givenADinosaurSpecie_whenUsingPolymorphism_thenGetMovementOfAnatotitan() {
		assertEquals("running", moveDinosaurUsingPolymorphism(new Anatotitan()));
	}

	@Test
	void givenADinosaurSpecie_whenUsingPolymorphism_thenGetMovementOfEuraptor() {
		assertEquals("flying", moveDinosaurUsingPolymorphism(new Euraptor()));
	}

	static String moveDinosaurUsingPolymorphism(Dinosaur dinosaur) {
		return dinosaur.move();
	}
}