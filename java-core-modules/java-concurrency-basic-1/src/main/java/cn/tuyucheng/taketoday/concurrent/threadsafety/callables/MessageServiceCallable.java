package cn.tuyucheng.taketoday.concurrent.threadsafety.callables;

import cn.tuyucheng.taketoday.concurrent.threadsafety.services.MessageService;

import java.util.concurrent.Callable;

public class MessageServiceCallable implements Callable<String> {

	private final MessageService messageService;

	public MessageServiceCallable(MessageService messageService) {
		this.messageService = messageService;
	}

	@Override
	public String call() {
		return messageService.getMessage();
	}
}