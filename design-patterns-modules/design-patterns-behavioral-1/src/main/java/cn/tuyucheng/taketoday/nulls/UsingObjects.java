package cn.tuyucheng.taketoday.nulls;

import java.util.Objects;

public class UsingObjects {

	public void accept(Object param) {
		Objects.requireNonNull(param);
		// doSomething()
	}
}