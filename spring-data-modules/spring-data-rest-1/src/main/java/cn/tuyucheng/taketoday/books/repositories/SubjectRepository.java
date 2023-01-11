package cn.tuyucheng.taketoday.books.repositories;

import cn.tuyucheng.taketoday.books.models.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface SubjectRepository extends PagingAndSortingRepository<Subject, Long> {

	@RestResource(path = "nameContains")
	Page<Subject> findByNameContaining(@Param("name") String name, Pageable p);
}