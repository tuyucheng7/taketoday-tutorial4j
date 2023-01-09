package cn.tuyucheng.taketoday.examples.guice;

import cn.tuyucheng.taketoday.examples.guice.constant.CommunicationModel;

public interface CommunicationMode {

	public CommunicationModel getMode();

	public boolean sendMessage(String message);

}
