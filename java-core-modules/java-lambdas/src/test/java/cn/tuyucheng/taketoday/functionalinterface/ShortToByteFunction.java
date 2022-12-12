package cn.tuyucheng.taketoday.functionalinterface;

@FunctionalInterface
public interface ShortToByteFunction {

	byte applyAsByte(short s);
}