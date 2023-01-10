package cn.tuyucheng.taketoday.cglib.mixin;

public class Class1 implements Interface1 {
	@Override
	public String first() {
		return "first behaviour";
	}
}