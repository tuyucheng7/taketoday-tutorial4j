package cn.tuyucheng.taketoday.recordswithjpa.repository;

import cn.tuyucheng.taketoday.recordswithjpa.embeddable.EmbeddableBook;
import cn.tuyucheng.taketoday.recordswithjpa.embeddable.Author;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EmbeddableBookRepository extends CrudRepository<EmbeddableBook, Long> {
   @Query("SELECT b FROM EmbeddableBook b WHERE b.author = :author")
   List<EmbeddableBook> findBookByAuthor(@Param("author") Author author);
}