package cn.tuyucheng.taketoday.examples.guice;

import cn.tuyucheng.taketoday.examples.guice.aop.MessageSentLoggable;
import cn.tuyucheng.taketoday.examples.guice.constant.CommunicationModel;
import com.google.inject.Inject;

import java.util.logging.Logger;
public class IMCommunicationMode implements CommunicationMode {

	@Inject
	private Logger logger;

	@Override
	public CommunicationModel getMode() {
		return CommunicationModel.IM;
	}

	@Override
	@MessageSentLoggable
	public boolean sendMessage(String message) {
		logger.info("IM Message Sent");
		return true;
	}

}
