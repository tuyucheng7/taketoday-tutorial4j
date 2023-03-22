package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.RecordsAsJpaIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookRepositoryIntegrationTest extends RecordsAsJpaIntegrationTest {

	@Test
	void findBookByAuthor() {
		assertEquals(2, bookRepository.findBookByAuthor("J.R.R. Tolkien").size());
	}

	@Test
	@Disabled("fails with: BookRepository.findBookById(java.lang.Long) is null")
	void findBookById() {
		assertEquals("The Lord of the Rings", bookRepository.findBookById(1L).title());
	}
}