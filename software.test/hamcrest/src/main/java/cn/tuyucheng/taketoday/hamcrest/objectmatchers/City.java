package cn.tuyucheng.taketoday.hamcrest.objectmatchers;

public class City extends Location {
	String name;
	String state;

	public City(String name, String state) {
		this.name = name;
		this.state = state;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	@Override
	public String toString() {
		if (this.name == null && this.state == null) return null;
		return "[" +
				"Name: " +
				this.name +
				", " +
				"State: " +
				this.state +
				"]";
	}
}