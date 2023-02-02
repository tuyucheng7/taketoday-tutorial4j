package cn.tuyucheng.taketoday.java14.patternmatchingforinstanceof;

import cn.tuyucheng.taketoday.java14.patternmatchingforinstanceof.PatternMatchingForInstanceOf.Cat;
import cn.tuyucheng.taketoday.java14.patternmatchingforinstanceof.PatternMatchingForInstanceOf.Dog;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PatternMatchingForInstanceOfUnitTest {

	@Test
	void givenAnAnimal_whenTypeIsCat_ThenCatGoesMeow() {
		Cat animal = mock(Cat.class);

		PatternMatchingForInstanceOf instanceOf = new PatternMatchingForInstanceOf();
		instanceOf.performAnimalOperations(animal);

		verify(animal).meow();
	}

	@Test
	void givenAnAnimal_whenTypeIsDog_ThenDogGoesWoof() {
		Dog animal = mock(Dog.class);

		PatternMatchingForInstanceOf instanceOf = new PatternMatchingForInstanceOf();
		instanceOf.performAnimalOperations(animal);

		verify(animal).woof();
	}
}