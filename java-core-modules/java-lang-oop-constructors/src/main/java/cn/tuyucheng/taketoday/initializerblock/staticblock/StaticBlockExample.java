package cn.tuyucheng.taketoday.initializerblock.staticblock;

public class StaticBlockExample {

	static {
		System.out.println("static block 1");
	}

	static {
		System.out.println("static block 2");
	}

	public static void main(String[] args) {
		System.out.println("Main Method");
	}
}

