package cn.tuyucheng.taketoday.callbackfunctions.synchronous;

import cn.tuyucheng.taketoday.callbackfunctions.EventListener;

public class SynchronousEventConsumer {

	private final EventListener eventListener;

	public SynchronousEventConsumer(EventListener listener) {
		this.eventListener = listener;
	}

	public String doSynchronousOperation() {
		System.out.println("Performing callback before synchronous Task");

		return eventListener.onTrigger();
	}


}
