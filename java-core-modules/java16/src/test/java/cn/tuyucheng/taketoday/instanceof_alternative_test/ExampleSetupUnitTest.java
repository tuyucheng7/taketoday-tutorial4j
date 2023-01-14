package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceof_alternatives.model.Anatotitan;
import cn.tuyucheng.taketoday.instanceof_alternatives.model.Dinosaur;
import cn.tuyucheng.taketoday.instanceof_alternatives.model.Euraptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleSetupUnitTest {

	@Test
	void givenADinosaurSpecie_whenUsingInstancof_thenGetMovementOfAnatotitan() {
		assertEquals("running", moveDinosaurUsingInstanceof(new Anatotitan()));
	}

	@Test
	void givenADinosaurSpecie_whenUsingInstanceof_thenGetMovementOfEuraptor() {
		assertEquals("flying", moveDinosaurUsingInstanceof(new Euraptor()));
	}

	static String moveDinosaurUsingInstanceof(Dinosaur dinosaur) {
		if (dinosaur instanceof Anatotitan) {
			Anatotitan anatotitan = (Anatotitan) dinosaur;
			return anatotitan.run();
		} else if (dinosaur instanceof Euraptor) {
			Euraptor euraptor = (Euraptor) dinosaur;
			return euraptor.flies();
		}
		return "";
	}
}