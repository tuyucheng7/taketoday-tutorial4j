package cn.tuyucheng.taketoday.quarkus.repository;

import cn.tuyucheng.taketoday.quarkus.utils.QuarkusTransactionalTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTransactionalTest
class BookRepositoryIntegrationTest {

	@Inject
	BookRepository bookRepository;

	@Test
	void givenBookInRepository_whenFindByAuthor_thenShouldReturnBookFromRepository() {
		assertTrue(bookRepository.findBy("Herbert").findAny().isPresent());
	}
}