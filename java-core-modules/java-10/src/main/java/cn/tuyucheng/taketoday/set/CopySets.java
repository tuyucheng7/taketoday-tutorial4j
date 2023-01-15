package cn.tuyucheng.taketoday.set;

import java.util.Set;

public class CopySets {

	// Using Java 10
	public static <T> Set<T> copyBySetCopyOf(Set<T> original) {
		return Set.copyOf(original);
	}
}