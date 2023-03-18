package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.RecordsAsJpaIntegrationTest;
import cn.tuyucheng.taketoday.recordswithjpa.records.CustomBookRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomBookRepositoryIntegrationTest extends RecordsAsJpaIntegrationTest {

	@Autowired
	private CustomBookRepository customBookRepository;

	@Test
	void findAllBooks() {
		List<CustomBookRecord> allBooks = customBookRepository.findAllBooks();
		assertEquals(3, allBooks.size());
	}
}