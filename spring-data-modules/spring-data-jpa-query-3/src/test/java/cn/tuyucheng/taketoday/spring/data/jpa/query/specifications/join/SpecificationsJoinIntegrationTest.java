package cn.tuyucheng.taketoday.spring.data.jpa.query.specifications.join;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static cn.tuyucheng.taketoday.spring.data.jpa.query.specifications.join.AuthorSpecifications.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SpecificationsJoinIntegrationTest {

    @Autowired
    private AuthorsRepository repository;

    @BeforeEach
    void beforeEach() {
        saveTestData();
    }

    @Test
    void whenSearchingByLastName_thenOneAuthorIsReturned() {
        List<Author> authors = repository.findAll(hasLastName("Martin"));

        Assertions.assertThat(authors).hasSize(1);
    }

    @Test
    void whenSearchingByLastNameAndFirstNameLike_thenOneAuthorIsReturned() {
        Specification<Author> specification = hasLastName("Martin").and(hasFirstNameLike("Robert"));

        List<Author> authors = repository.findAll(specification);

        Assertions.assertThat(authors).hasSize(1);
    }

    @Test
    void whenSearchingByBookTitle_thenOneAuthorIsReturned() {
        Specification<Author> specification = hasBookWithTitle("Clean Code");

        List<Author> authors = repository.findAll(specification);

        Assertions.assertThat(authors).hasSize(1);
    }

    @Test
    void whenSearchingByBookTitleAndAuthorName_thenOneAuthorIsReturned() {
        Specification<Author> specification = hasLastName("Martin").and(hasBookWithTitle("Clean Code"));

        List<Author> authors = repository.findAll(specification);

        Assertions.assertThat(authors).hasSize(1);
    }

    private void saveTestData() {
        Author uncleBob = new Author();
        uncleBob.setFirstName("Robert");
        uncleBob.setLastName("Martin");

        Book book1 = new Book();
        book1.setTitle("Clean Code");
        Book book2 = new Book();
        book2.setTitle("Clean Architecture");

        uncleBob.setBooks(Arrays.asList(book1, book2));
        repository.save(uncleBob);
    }
}