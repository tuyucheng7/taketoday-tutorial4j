package cn.tuyucheng.taketoday.generics;

public class Dog extends Animal {

	public Dog(String type, String name) {
		super(type, name);
	}

	@Override
	public String makeSound() {
		return "Wuf";
	}

}
