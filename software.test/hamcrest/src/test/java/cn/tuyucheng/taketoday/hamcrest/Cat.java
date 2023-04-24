package cn.tuyucheng.taketoday.hamcrest;

public class Cat extends Animal {

	public Cat() {
		super("cat", false, "meow");
	}

	public String makeSound() {
		return getSound();
	}

}
