package cn.tuyucheng.taketoday.pointcutadvice.events;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class FooCreationEventListener implements ApplicationListener<FooCreationEvent> {

	private static final Logger logger = Logger.getLogger(FooCreationEventListener.class.getName());

	@Override
	public void onApplicationEvent(FooCreationEvent event) {
		logger.info("Created foo instance: " + event.getSource().toString());
	}
}