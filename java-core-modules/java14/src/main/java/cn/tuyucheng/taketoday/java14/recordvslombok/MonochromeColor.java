package cn.tuyucheng.taketoday.java14.recordvslombok;

import lombok.Value;

@Value
public class MonochromeColor extends ColorData {

	public MonochromeColor(int grayScale) {
		super(grayScale, grayScale, grayScale);
	}
}