package cn.tuyucheng.taketoday.kotlindsl;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Sorter {

	public List<Reporter.StockPrice> sort(List<Reporter.StockPrice> list) {
		return list.stream()
			.sorted(comparing(it -> it.timestamp))
			.collect(toList());
	}
}