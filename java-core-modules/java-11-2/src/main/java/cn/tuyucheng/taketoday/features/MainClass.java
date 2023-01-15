package cn.tuyucheng.taketoday.features;

public class MainClass {

	private static boolean mainPrivateMethod() {
		return true;
	}

	public static class NestedClass {
		boolean nestedPublicMethod() {
			return mainPrivateMethod();
		}

	}
}