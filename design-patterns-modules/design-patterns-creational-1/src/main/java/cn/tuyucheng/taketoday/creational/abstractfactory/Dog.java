package cn.tuyucheng.taketoday.creational.abstractfactory;

public class Dog implements Animal {

	@Override
	public String getType() {
		return "Dog";
	}

	@Override
	public String makeSound() {
		return "Barks";
	}
}