package cn.tuyucheng.taketoday.exceptions;

public class UserWithNoDefaultConstructor {

	private int id;
	private String name;

	public UserWithNoDefaultConstructor(final int id, final String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}