package cn.tuyucheng.taketoday.callbackfunctions.asynchronous;

import cn.tuyucheng.taketoday.callbackfunctions.EventListener;

public class AsynchronousEventConsumer {

	private EventListener listener;

	public AsynchronousEventConsumer(EventListener listener) {
		this.listener = listener;
	}

	public void doAsynchronousOperation() {
		System.out.println("Performing operation in Asynchronous Task");

		new Thread(() -> listener.onTrigger()).start();
	}


}
