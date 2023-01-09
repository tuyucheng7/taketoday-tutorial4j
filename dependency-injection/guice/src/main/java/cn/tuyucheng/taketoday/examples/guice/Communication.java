package cn.tuyucheng.taketoday.examples.guice;

import com.google.inject.Inject;

import java.util.Date;
import java.util.logging.Logger;
public class Communication {

	final Date start = new Date();

	@Inject
	private Logger logger;

	@Inject
	private DefaultCommunicator communicator;

	public Communication(Boolean keepRecords) {
		if (keepRecords) {
			System.out.println("keeping records");
		}
	}

	public boolean sendMessage(String message) {

		return communicator.sendMessage(message);
	}

	public DefaultCommunicator getCommunicator() {
		return this.communicator;
	}

}
