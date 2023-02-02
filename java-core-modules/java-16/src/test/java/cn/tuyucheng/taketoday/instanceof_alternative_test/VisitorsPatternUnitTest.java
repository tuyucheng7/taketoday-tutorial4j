package cn.tuyucheng.taketoday.instanceof_alternative_test;

import cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern.Anatotitan;
import cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern.Dino;
import cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern.DinoVisitorImpl;
import cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern.Euraptor;
import cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern.Visitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorsPatternUnitTest {

	@Test
	void givenADinosaurSpecie_whenUsingVisitorPattern_thenGetMovementOfAnatotitan() {
		assertEquals("running", moveDinosaurUsingVisitorPattern((Dino) new Anatotitan()));
	}

	@Test
	void givenADinosaurSpecie_whenUsingVisitorPattern_thenGetMovementOfEuraptor() {
		assertEquals("flying", moveDinosaurUsingVisitorPattern((Dino) new Euraptor()));
	}

	static String moveDinosaurUsingVisitorPattern(Dino dinosaur) {
		Visitor visitor = new DinoVisitorImpl();

		return dinosaur.move(visitor);
	}
}