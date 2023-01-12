package org.tuyucheng.taketoday.conditionalflow.step;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class PrependingStdoutWriter<T> implements ItemWriter<T> {
	private String prependText;

	public PrependingStdoutWriter(String prependText) {
		this.prependText = prependText;
	}

	@Override
	public void write(List<? extends T> list) {
		for (T listItem : list) {
			System.out.println(prependText + " " + listItem.toString());
		}
	}
}
