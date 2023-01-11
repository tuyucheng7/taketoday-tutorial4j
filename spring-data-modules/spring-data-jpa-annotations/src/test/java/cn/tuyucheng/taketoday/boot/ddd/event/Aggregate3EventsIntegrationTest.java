package cn.tuyucheng.taketoday.boot.ddd.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
@SpringBootTest
class Aggregate3EventsIntegrationTest {

	@MockBean
	private TestEventHandler eventHandler;
	@Autowired
	private Aggregate3Repository repository;

	@DisplayName("given aggregate extending AbstractAggregateRoot, when do domain operation and save twice, then an event is published only for the first time")
	@Test
	void afterDomainEvents() {
		// given
		Aggregate3 aggregate = new Aggregate3();

		// when
		aggregate.domainOperation();
		repository.save(aggregate);
		repository.save(aggregate);

		// then
		verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
	}

	@DisplayName("given aggregate extending AbstractAggregateRoot, when do domain operation and save, then an event is published")
	@Test
	void domainEvents() {
		// given
		Aggregate3 aggregate = new Aggregate3();

		// when
		aggregate.domainOperation();
		repository.save(aggregate);

		// then
		verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
	}
}