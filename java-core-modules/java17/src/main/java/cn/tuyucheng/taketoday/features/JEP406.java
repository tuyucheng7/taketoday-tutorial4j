package cn.tuyucheng.taketoday.features;

public class JEP406 {

	record Human(String name, int age, String profession) {
	}

	public String checkObject(Object obj) {
		return switch (obj) {
			case Human h -> "Name: %s, age: %s and profession: %s".formatted(h.name(), h.age(), h.profession());
			case JEP409.Circle c -> "This is a circle";
			case JEP409.Shape s -> "It is just a shape";
			case null -> "It is null";
			default -> "It is an object";
		};
	}

	public String checkShape(JEP409.Shape shape) {
		return switch (shape) {
			case JEP409.Triangle t && (t.getNumberOfSides() != 3) -> "This is a weird triangle";
			case JEP409.Circle c && (c.getNumberOfSides() != 0) -> "This is a weird circle";
			default -> "Just a normal shape";
		};
	}
}