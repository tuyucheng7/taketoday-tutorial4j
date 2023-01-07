package cn.tuyucheng.taketoday.constructorspecification.rules;

class RecursiveConstructorInvocation {

	public RecursiveConstructorInvocation() {
		RecursiveConstructorInvocation rci = new RecursiveConstructorInvocation();
	}

	public static void main(String[] args) {
		new RecursiveConstructorInvocation(); // java.lang.StackOverflowError
	}
}