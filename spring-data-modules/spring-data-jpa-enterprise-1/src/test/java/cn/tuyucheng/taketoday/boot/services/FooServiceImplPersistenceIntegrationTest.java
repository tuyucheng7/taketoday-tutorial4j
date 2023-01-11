package cn.tuyucheng.taketoday.boot.services;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.domain.Foo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
class FooServiceImplPersistenceIntegrationTest extends AbstractServicePersistenceIntegrationTest<Foo> {

	@Autowired
	private FooService service;

	@Test
	final void whenContextIsBootstrapped_thenNoExceptions() {
		//
	}

	@Test
	final void whenEntityIsCreated_thenNoExceptions() {
		service.create(new Foo(randomAlphabetic(6)));
	}

	@Test
	final void whenInvalidEntityIsCreated_thenDataException() {
		assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo()));
	}

	@Test
	final void whenEntityWithLongNameIsCreated_thenDataException() {
		assertThrows(DataIntegrityViolationException.class, () -> service.create(new Foo(randomAlphabetic(2048))));
	}

	@Test
	final void givenUsingCustomQuery_whenRetrievingEntity_thenFound() {
		final String name = randomAlphabetic(6);
		service.create(new Foo(name));

		final Foo retrievedByName = service.retrieveByName(name);
		assertNotNull(retrievedByName);
	}

	@Test
	@Disabled("Right now, persist has saveOrUpdate semantics, so this will no longer fail")
	final void whenSameEntityIsCreatedTwice_thenDataException() {
		final Foo entity = new Foo(randomAlphabetic(8));
		service.create(entity);
		assertThrows(InvalidDataAccessApiUsageException.class, () -> service.create(entity));
	}

	@Override
	protected final Operations<Foo> getApi() {
		return service;
	}
}