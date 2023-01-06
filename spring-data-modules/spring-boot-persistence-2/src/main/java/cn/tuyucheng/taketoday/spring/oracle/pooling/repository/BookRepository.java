package cn.tuyucheng.taketoday.spring.oracle.pooling.repository;

import cn.tuyucheng.taketoday.spring.oracle.pooling.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}