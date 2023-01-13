package cn.tuyucheng.taketoday.gson.deserialization;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class FooInstanceCreator implements InstanceCreator<Foo> {

	@Override
	public Foo createInstance(Type type) {
		return new Foo("sample");
	}

}
