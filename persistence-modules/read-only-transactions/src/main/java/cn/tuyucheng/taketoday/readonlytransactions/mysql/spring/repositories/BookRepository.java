package cn.tuyucheng.taketoday.readonlytransactions.mysql.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.tuyucheng.taketoday.readonlytransactions.mysql.spring.ReaderDS;
import cn.tuyucheng.taketoday.readonlytransactions.mysql.spring.entities.BookEntity;

import javax.transaction.Transactional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

   @ReaderDS
   @Query("Select 1 from BookEntity t where t.id = ?1")
   Long get(Long id);

   @Transactional
   default BookEntity persist(BookEntity book) {
      return this.save(book);
   }
}