package cn.tuyucheng.taketoday.boot.ddd.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@SpringBootTest
class AggregateEventsIntegrationTest {

	@Autowired
	private DomainService domainService;

	@MockBean
	private TestEventHandler eventHandler;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private AggregateRepository repository;

	@DisplayName("given existing aggregate, when do domain operation directly on aggregate, then domain event is NOT published")
	@Test
	void aggregateEventsTest() {
		Aggregate existingDomainEntity = new Aggregate(0, eventPublisher);
		repository.save(existingDomainEntity);

		// when
		repository.findById(existingDomainEntity.getId())
				.get()
				.domainOperation();

		// then
		verifyNoInteractions(eventHandler);
	}

	@BeforeEach
	void beforeEach() {
		repository.deleteAll();
	}

	@DisplayName("given existing aggregate, when do domain operation on service, then domain event is published")
	@Test
	void serviceEventsTest() {
		Aggregate existingDomainEntity = new Aggregate(1, eventPublisher);
		repository.save(existingDomainEntity);

		// when
		domainService.serviceDomainOperation(existingDomainEntity.getId());

		// then
		verify(eventHandler, times(1)).handleEvent(any(DomainEvent.class));
	}

	@TestConfiguration
	public static class TestConfig {
		@Bean
		public DomainService domainService(AggregateRepository repository, ApplicationEventPublisher eventPublisher) {
			return new DomainService(repository, eventPublisher);
		}
	}
}