package cn.tuyucheng.taketoday.eclipsecollections;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;

public class ConvertContainerToAnother {

	@SuppressWarnings("rawtypes")
	public static List convertToList() {
		UnifiedSet<String> cars = new UnifiedSet<>();

		cars.add("Toyota");
		cars.add("Mercedes");
		cars.add("Volkswagen");

		return cars.toList();
	}
}
