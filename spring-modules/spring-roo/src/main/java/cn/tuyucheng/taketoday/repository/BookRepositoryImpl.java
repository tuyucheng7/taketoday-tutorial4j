package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.domain.Book;
import io.springlets.data.jpa.repository.support.QueryDslRepositorySupportExt;
import org.springframework.roo.addon.layers.repository.jpa.annotations.RooJpaRepositoryCustomImpl;

/**
 * = BookRepositoryImpl
 * <p>
 * TODO Auto-generated class documentation
 */
@RooJpaRepositoryCustomImpl(repository = BookRepositoryCustom.class)
public class BookRepositoryImpl extends QueryDslRepositorySupportExt<Book> {

	/**
	 * TODO Auto-generated constructor documentation
	 */
	BookRepositoryImpl() {
		super(Book.class);
	}
}