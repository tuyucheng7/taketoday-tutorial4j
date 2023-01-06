package cn.tuyucheng.taketoday.springboothibernate.application.repositories;

import cn.tuyucheng.taketoday.springboothibernate.application.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}