package cn.tuyucheng.taketoday.datajpadelete;

import cn.tuyucheng.taketoday.Application;
import cn.tuyucheng.taketoday.datajpadelete.entity.Book;
import cn.tuyucheng.taketoday.datajpadelete.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class DeleteFromRepositoryUnitTest {

	@Autowired
	private BookRepository repository;

	Book book1;
	Book book2;

	@BeforeEach
	void setup() {
		book1 = new Book("The Hobbit");
		book2 = new Book("All Quiet on the Western Front");

		repository.saveAll(Arrays.asList(book1, book2));
	}

	@AfterEach
	void teardown() {
		repository.deleteAll();
	}

	@Test
	void whenDeleteByIdFromRepository_thenDeletingShouldBeSuccessful() {
		repository.deleteById(book1.getId());

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void whenDeleteAllFromRepository_thenRepositoryShouldBeEmpty() {
		repository.deleteAll();

		assertThat(repository.count()).isEqualTo(0);
	}

	@Test
	@Transactional
	void whenDeleteFromDerivedQuery_thenDeletingShouldBeSuccessful() {
		long deletedRecords = repository.deleteByTitle("The Hobbit");

		assertThat(deletedRecords).isEqualTo(1);
	}

	@Test
	@Transactional
	void whenDeleteFromCustomQuery_thenDeletingShouldBeSuccessful() {
		repository.deleteBooks("The Hobbit");

		assertThat(repository.count()).isEqualTo(1);
	}
}