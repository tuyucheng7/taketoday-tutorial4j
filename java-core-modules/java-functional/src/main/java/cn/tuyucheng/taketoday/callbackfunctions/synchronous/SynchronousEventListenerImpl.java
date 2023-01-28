package cn.tuyucheng.taketoday.callbackfunctions.synchronous;

import cn.tuyucheng.taketoday.callbackfunctions.EventListener;

public class SynchronousEventListenerImpl implements EventListener {

	@Override
	public String onTrigger() {
		return "Synchronously running callback function";
	}

	@Override
	public void respondToTrigger() {
		System.out.println("Response to trigger");
	}

}
