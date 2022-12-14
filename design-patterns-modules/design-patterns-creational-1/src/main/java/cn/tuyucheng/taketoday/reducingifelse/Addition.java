package cn.tuyucheng.taketoday.reducingifelse;

public class Addition implements Operation {
	@Override
	public int apply(int a, int b) {
		return a + b;
	}
}