package cn.tuyucheng.taketoday.spring.insertableonly.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CustomQueryBookRepository extends JpaRepository<CustomQueryBook, Long> {

   @Modifying
   @Transactional
   @Query(value = "INSERT INTO custom_query_book (id, title) VALUES (:#{#book.id}, :#{#book.title})", nativeQuery = true)
   void persist(@Param("book") CustomQueryBook book);
}