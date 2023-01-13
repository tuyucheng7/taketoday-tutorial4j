package cn.tuyucheng.taketoday.mdc.log4j2;

import cn.tuyucheng.taketoday.mdc.Transfer;
import org.apache.logging.log4j.ThreadContext;

public class Log4J2Runnable implements Runnable {
	private final Transfer tx;
	private Log4J2TransferService log4j2BusinessService = new Log4J2TransferService();

	public Log4J2Runnable(Transfer tx) {
		this.tx = tx;
	}

	public void run() {

		ThreadContext.put("transaction.id", tx.getTransactionId());
		ThreadContext.put("transaction.owner", tx.getSender());

		log4j2BusinessService.transfer(tx.getAmount());

		ThreadContext.clearAll();

	}
}