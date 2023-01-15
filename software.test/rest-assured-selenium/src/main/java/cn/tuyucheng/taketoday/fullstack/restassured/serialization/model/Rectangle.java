package cn.tuyucheng.taketoday.fullstack.restassured.serialization.model;

import java.io.Serial;
import java.io.Serializable;

public class Rectangle implements Serializable {
	@Serial
	private static final long serialVersionUID = 1624256616363L;

	private final double height;
	private final double width;

	public Rectangle(double height, double width) {
		this.height = height;
		this.width = width;
	}

	public double Area() {
		return height * width;
	}

	public double Perimeter() {
		return 2 * (height + width);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}
}