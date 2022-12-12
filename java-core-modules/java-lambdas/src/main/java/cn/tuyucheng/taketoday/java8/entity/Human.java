package cn.tuyucheng.taketoday.java8.entity;

public class Human {
	private String name;
	private int age;

	public Human() {
		super();
	}

	public Human(final String name, final int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(final int age) {
		this.age = age;
	}

	public static int compareByNameThenAge(final Human lhs, final Human rhs) {
		if (lhs.name.equals(rhs.name)) {
			return Integer.compare(lhs.age, rhs.age);
		} else {
			return lhs.name.compareTo(rhs.name);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Human other = (Human) obj;
		if (age != other.age) {
			return false;
		}
		if (name == null) {
			return other.name == null;
		} else return name.equals(other.name);
	}

	@Override
	public String toString() {
		return "Human [name=" + name + ", age=" + age + "]";
	}
}