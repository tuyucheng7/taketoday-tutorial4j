package cn.tuyucheng.taketoday.batch.service;

import cn.tuyucheng.taketoday.batch.model.Transaction;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Transaction, Transaction> {

	public Transaction process(Transaction item) {
		System.out.println("Processing..." + item);
		return item;
	}
}