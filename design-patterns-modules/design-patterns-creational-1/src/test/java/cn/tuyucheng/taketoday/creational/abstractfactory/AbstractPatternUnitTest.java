package cn.tuyucheng.taketoday.creational.abstractfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractPatternUnitTest {
	@Test
	void givenAbstractFactory_whenGettingBrownDogObjects_thenSuccessful() {
		AbstractFactory abstractFactory;

		// creating a brown toy dog
		abstractFactory = FactoryProvider.getFactory("Toy");
		Animal toy = (Animal) abstractFactory.create("Dog");

		abstractFactory = FactoryProvider.getFactory("Color");
		Color color = (Color) abstractFactory.create("Brown");

		String result = "A " + toy.getType() + " with " + color.getColor() + " color " + toy.makeSound();
		assertEquals("A Dog with brown color Barks", result);
	}

	@Test
	void givenAbstractFactory_whenGettingWhiteDuckObjects_thenSuccessful() {
		AbstractFactory abstractFactory;

		// creating a brown toy Duck
		abstractFactory = FactoryProvider.getFactory("Toy");
		Animal toy = (Animal) abstractFactory.create("Duck");

		abstractFactory = FactoryProvider.getFactory("Color");
		Color color = (Color) abstractFactory.create("White");

		String result = "A " + toy.getType() + " with " + color.getColor() + " color " + toy.makeSound();
		assertEquals("A Duck with white color Squeks", result);
	}
}