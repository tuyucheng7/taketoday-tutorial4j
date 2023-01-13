package cn.tuyucheng.taketoday.mdc.log4j;

import cn.tuyucheng.taketoday.mdc.TransferService;
import org.apache.log4j.Logger;

public class Log4JTransferService extends TransferService {

	private Logger logger = Logger.getLogger(Log4JTransferService.class);

	@Override
	protected void beforeTransfer(long amount) {
		logger.info("Preparing to transfer " + amount + "$.");
	}

	@Override
	protected void afterTransfer(long amount, boolean outcome) {
		logger.info("Has transfer of " + amount + "$ completed successfully ? " + outcome + ".");
	}

}