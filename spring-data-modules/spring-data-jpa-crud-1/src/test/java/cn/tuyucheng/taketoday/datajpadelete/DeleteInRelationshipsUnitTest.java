package cn.tuyucheng.taketoday.datajpadelete;

import cn.tuyucheng.taketoday.Application;
import cn.tuyucheng.taketoday.datajpadelete.entity.Book;
import cn.tuyucheng.taketoday.datajpadelete.entity.Category;
import cn.tuyucheng.taketoday.datajpadelete.repository.BookRepository;
import cn.tuyucheng.taketoday.datajpadelete.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class DeleteInRelationshipsUnitTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setup() {
		Book book1 = new Book("The Hobbit");
		Category category1 = new Category("Cat1", book1);
		categoryRepository.save(category1);

		Book book2 = new Book("All Quiet on the Western Front");
		Category category2 = new Category("Cat2", book2);
		categoryRepository.save(category2);
	}

	@AfterEach
	void teardown() {
		bookRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Test
	void whenDeletingCategories_thenBooksShouldAlsoBeDeleted() {
		categoryRepository.deleteAll();

		assertThat(bookRepository.count()).isEqualTo(0);
		assertThat(categoryRepository.count()).isEqualTo(0);
	}

	@Test
	void whenDeletingBooks_thenCategoriesShouldAlsoBeDeleted() {
		bookRepository.deleteAll();

		assertThat(bookRepository.count()).isEqualTo(0);
		assertThat(categoryRepository.count()).isEqualTo(2);
	}
}