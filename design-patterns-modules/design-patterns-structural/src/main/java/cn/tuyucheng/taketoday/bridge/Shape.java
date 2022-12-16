package cn.tuyucheng.taketoday.bridge;

public abstract class Shape {
	protected Color color;

	protected Shape(Color color) {
		this.color = color;
	}

	public abstract String draw();
}