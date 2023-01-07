package cn.tuyucheng.taketoday.constructorspecification.rules;

class Person {

	String name;

	public Person() {
		this("Arash");
	}

	public Person(String name) {
		this.name = name;
	}
}