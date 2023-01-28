package cn.tuyucheng.taketoday.callbackfunctions.asynchronous;

import cn.tuyucheng.taketoday.callbackfunctions.EventListener;

public class AsynchronousEventListenerImpl implements EventListener {

	@Override
	public String onTrigger() {
		respondToTrigger();
		return "Asynchronously running callback function";
	}

	@Override
	public void respondToTrigger() {
		System.out.println("This is a side effect of the asynchronous trigger.");
	}

}
