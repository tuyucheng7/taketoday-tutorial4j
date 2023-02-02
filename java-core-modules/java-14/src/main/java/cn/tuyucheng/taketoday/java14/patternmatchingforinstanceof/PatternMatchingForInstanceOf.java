package cn.tuyucheng.taketoday.java14.patternmatchingforinstanceof;

public class PatternMatchingForInstanceOf {

	public void performAnimalOperations(Animal animal) {
		if (animal instanceof Cat cat) {
			cat.meow();
		} else if (animal instanceof Dog dog) {
			dog.woof();
		}
	}

	abstract class Animal {
	}

	class Cat extends Animal {
		void meow() {
		}
	}

	class Dog extends Animal {
		void woof() {
		}
	}
}