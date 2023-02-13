package cn.tuyucheng.taketoday.failureanalyzer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.tuyucheng.taketoday.failureanalyzer.utils.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FailureAnalyzerAppIntegrationTest {

	private static final String EXPECTED_ANALYSIS_DESCRIPTION_TITLE = "Description:";
	private static final String EXPECTED_ANALYSIS_DESCRIPTION_CONTENT = "The bean myDAO could not be injected as cn.tuyucheng.taketoday.failureanalyzer.MyDAO because it is of type cn.tuyucheng.taketoday.failureanalyzer.MySecondDAO";
	private static final String EXPECTED_ANALYSIS_ACTION_TITLE = "Action:";
	private static final String EXPECTED_ANALYSIS_ACTION_CONTENT = "Consider creating a bean with name myDAO of type cn.tuyucheng.taketoday.failureanalyzer.MyDAO";

	@BeforeEach
	void clearLogList() {
		ListAppender.clearEventList();
	}

	@Test
	void givenBeanCreationErrorInContext_whenContextLoaded_thenFailureAnalyzerLogsReport() {
		try {
			SpringApplication.run(FailureAnalyzerApplication.class);
		} catch (BeanCreationException e) {
			Collection<String> allLoggedEntries = ListAppender.getEvents()
				.stream()
				.map(ILoggingEvent::getFormattedMessage)
				.collect(Collectors.toList());
			assertThat(allLoggedEntries).anyMatch(entry -> entry.contains(EXPECTED_ANALYSIS_DESCRIPTION_TITLE))
				.anyMatch(entry -> entry.contains(EXPECTED_ANALYSIS_DESCRIPTION_CONTENT))
				.anyMatch(entry -> entry.contains(EXPECTED_ANALYSIS_ACTION_TITLE))
				.anyMatch(entry -> entry.contains(EXPECTED_ANALYSIS_ACTION_CONTENT));
			return;
		}
		throw new IllegalStateException("Context load should be failing due to a BeanCreationException!");
	}
}