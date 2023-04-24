package cn.tuyucheng.taketoday.proxy;

public class ProxyPatternDriver {
	public static void main(String[] args) {
		ExpensiveObject object = new ExpensiveObjectProxy();
		object.process();
		object.process();
	}
}