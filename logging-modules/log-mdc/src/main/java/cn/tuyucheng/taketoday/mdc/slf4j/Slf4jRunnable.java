package cn.tuyucheng.taketoday.mdc.slf4j;

import cn.tuyucheng.taketoday.mdc.Transfer;
import org.slf4j.MDC;

public class Slf4jRunnable implements Runnable {
	private final Transfer tx;

	public Slf4jRunnable(Transfer tx) {
		this.tx = tx;
	}

	public void run() {

		MDC.put("transaction.id", tx.getTransactionId());
		MDC.put("transaction.owner", tx.getSender());

		new Slf4TransferService().transfer(tx.getAmount());

		// MDC.clear(); We don't need this with MdcAwareThreadPoolExecutor

	}
}