package cn.tuyucheng.taketoday.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCustomTestAppender extends AppenderBase<ILoggingEvent> {

	private final List<ILoggingEvent> logs = new ArrayList<>();

	public InMemoryCustomTestAppender() {
		((Logger) LoggerFactory.getLogger("root")).addAppender(this);
		start();
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		logs.add(eventObject);
	}

	public boolean logContains(String message) {
		return logs.stream().anyMatch(event -> event.getFormattedMessage().equals(message));
	}
}