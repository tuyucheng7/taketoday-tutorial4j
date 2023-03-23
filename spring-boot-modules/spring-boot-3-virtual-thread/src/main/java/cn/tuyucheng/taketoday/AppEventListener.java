package cn.tuyucheng.taketoday;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AppEventListener {

	@EventListener
	@Async
	public void onGreetingEvent(String message) {
		LOGGER.info(Thread.currentThread() + " :: Received: {}", message);
	}
}