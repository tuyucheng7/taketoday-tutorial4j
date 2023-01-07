package cn.tuyucheng.taketoday.accessmodifiers;

public class SubClass extends SuperPublic {
	public SubClass() {
		publicMethod(); // Available everywhere.
		protectedMethod(); // Available in the same package or subclass.
		defaultMethod(); // Available in the same package.
	}
}
