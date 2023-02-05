package cn.tuyucheng.taketoday.interfaceVsAbstractClass;

import java.util.Arrays;
import java.util.List;

public abstract class CircleClass {

	private String color;
	private final List<String> allowedColors = Arrays.asList("RED", "GREEN", "BLUE");

	public boolean isValid() {
		return allowedColors.contains(getColor());
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}