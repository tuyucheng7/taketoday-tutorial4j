package cn.tuyucheng.taketoday.stream.sum;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamSumUnitTest {

	@Test
	void givenListOfIntegersWhenSummingUsingCustomizedAccumulatorThenCorrectValueReturned() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Integer sum = StreamSumCalculator.getSumUsingCustomizedAccumulator(integers);
		assertEquals(15, sum.intValue());

	}

	@Test
	void givenListOfIntegersWhenSummingUsingJavaAccumulatorThenCorrectValueReturned() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Integer sum = StreamSumCalculator.getSumUsingJavaAccumulator(integers);
		assertEquals(15, sum.intValue());
	}

	@Test
	void givenListOfIntegersWhenSummingUsingReduceThenCorrectValueReturned() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Integer sum = StreamSumCalculator.getSumUsingReduce(integers);
		assertEquals(15, sum.intValue());
	}

	@Test
	void givenListOfIntegersWhenSummingUsingCollectThenCorrectValueReturned() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Integer sum = StreamSumCalculator.getSumUsingCollect(integers);
		assertEquals(15, sum.intValue());
	}

	@Test
	void givenListOfIntegersWhenSummingUsingSumThenCorrectValueReturned() {
		List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
		Integer sum = StreamSumCalculator.getSumUsingSum(integers);
		assertEquals(15, sum.intValue());
	}

	@Test
	void givenListOfItemsWhenSummingUsingCustomizedAccumulatorThenCorrectValueReturned() {
		Item item1 = new Item(1, 10);
		Item item2 = new Item(2, 15);
		Item item3 = new Item(3, 25);
		Item item4 = new Item(4, 40);

		List<Item> items = Arrays.asList(item1, item2, item3, item4);

		Integer sum = StreamSumCalculatorWithObject.getSumUsingCustomizedAccumulator(items);
		assertEquals(90, sum.intValue());

	}

	@Test
	void givenListOfItemsWhenSummingUsingJavaAccumulatorThenCorrectValueReturned() {
		Item item1 = new Item(1, 10);
		Item item2 = new Item(2, 15);
		Item item3 = new Item(3, 25);
		Item item4 = new Item(4, 40);

		List<Item> items = Arrays.asList(item1, item2, item3, item4);

		Integer sum = StreamSumCalculatorWithObject.getSumUsingJavaAccumulator(items);
		assertEquals(90, sum.intValue());
	}

	@Test
	void givenListOfItemsWhenSummingUsingReduceThenCorrectValueReturned() {
		Item item1 = new Item(1, 10);
		Item item2 = new Item(2, 15);
		Item item3 = new Item(3, 25);
		Item item4 = new Item(4, 40);

		List<Item> items = Arrays.asList(item1, item2, item3, item4);

		Integer sum = StreamSumCalculatorWithObject.getSumUsingReduce(items);
		assertEquals(90, sum.intValue());
	}

	@Test
	void givenListOfItemsWhenSummingUsingCollectThenCorrectValueReturned() {
		Item item1 = new Item(1, 10);
		Item item2 = new Item(2, 15);
		Item item3 = new Item(3, 25);
		Item item4 = new Item(4, 40);

		List<Item> items = Arrays.asList(item1, item2, item3, item4);

		Integer sum = StreamSumCalculatorWithObject.getSumUsingCollect(items);
		assertEquals(90, sum.intValue());
	}

	@Test
	void givenListOfItemsWhenSummingUsingSumThenCorrectValueReturned() {
		Item item1 = new Item(1, 10);
		Item item2 = new Item(2, 15);
		Item item3 = new Item(3, 25);
		Item item4 = new Item(4, 40);

		List<Item> items = Arrays.asList(item1, item2, item3, item4);

		Integer sum = StreamSumCalculatorWithObject.getSumUsingSum(items);
		assertEquals(90, sum.intValue());
	}

	@Test
	void givenMapWhenSummingThenCorrectValueReturned() {
		Map<Object, Integer> map = new HashMap<Object, Integer>();
		map.put(1, 10);
		map.put(2, 15);
		map.put(3, 25);
		map.put(4, 40);

		Integer sum = StreamSumCalculator.getSumOfMapValues(map);
		assertEquals(90, sum.intValue());
	}

	@Test
	void givenStringWhenSummingThenCorrectValueReturned() {
		String string = "Item1 10 Item2 25 Item3 30 Item4 45";

		Integer sum = StreamSumCalculator.getSumIntegersFromString(string);
		assertEquals(110, sum.intValue());
	}
}