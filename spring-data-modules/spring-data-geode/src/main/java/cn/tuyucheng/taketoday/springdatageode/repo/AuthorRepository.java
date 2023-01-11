package cn.tuyucheng.taketoday.springdatageode.repo;

import cn.tuyucheng.taketoday.springdatageode.domain.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}