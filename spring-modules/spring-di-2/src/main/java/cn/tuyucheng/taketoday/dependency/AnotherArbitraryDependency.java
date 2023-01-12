package cn.tuyucheng.taketoday.dependency;

import org.springframework.stereotype.Component;

@Component
public class AnotherArbitraryDependency extends ArbitraryDependency {

	private final String label = "Another Arbitrary Dependency";

	public String toString() {
		return label;
	}
}
