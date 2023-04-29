package org.tuyucheng.taketoday.conditionalflow.step;

import org.springframework.batch.item.ItemReader;
import org.tuyucheng.taketoday.conditionalflow.model.NumberInfo;

public class NumberInfoGenerator implements ItemReader<NumberInfo> {
	private int[] values;
	private int counter;

	public NumberInfoGenerator(int[] values) {
		this.values = values;
		counter = 0;
	}

	@Override
	public NumberInfo read() {
		if (counter == values.length) {
			return null;
		} else {
			return new NumberInfo(values[counter++]);
		}
	}
}
