package cn.tuyucheng.taketoday.concurrent.daemon;

public class SingleThreadExample {

	public static void main(String[] args) {
		NewThread t = new NewThread();
		t.start();
	}
}