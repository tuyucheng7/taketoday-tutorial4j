package cn.tuyucheng.taketoday.examples.guice;

import cn.tuyucheng.taketoday.examples.guice.aop.MessageSentLoggable;
import cn.tuyucheng.taketoday.examples.guice.constant.CommunicationModel;
public class EmailCommunicationMode implements CommunicationMode {

	@Override
	public CommunicationModel getMode() {
		return CommunicationModel.EMAIL;
	}

	@Override
	@MessageSentLoggable
	public boolean sendMessage(String Message) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose Tools | Templates.
	}

}
