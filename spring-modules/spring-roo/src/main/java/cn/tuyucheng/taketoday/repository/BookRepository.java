package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.domain.Book;
import org.springframework.roo.addon.layers.repository.jpa.annotations.RooJpaRepository;

/**
 * = BookRepository
 * TODO Auto-generated class documentation
 */
@RooJpaRepository(entity = Book.class)
public interface BookRepository {
}
