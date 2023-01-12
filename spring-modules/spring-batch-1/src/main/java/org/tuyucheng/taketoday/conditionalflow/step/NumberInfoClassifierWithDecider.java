package org.tuyucheng.taketoday.conditionalflow.step;

import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.tuyucheng.taketoday.conditionalflow.model.NumberInfo;

public class NumberInfoClassifierWithDecider extends ItemListenerSupport<NumberInfo, Integer> implements ItemProcessor<NumberInfo, Integer> {

	@Override
	public Integer process(NumberInfo numberInfo) throws Exception {
		return Integer.valueOf(numberInfo.getNumber());
	}
}
