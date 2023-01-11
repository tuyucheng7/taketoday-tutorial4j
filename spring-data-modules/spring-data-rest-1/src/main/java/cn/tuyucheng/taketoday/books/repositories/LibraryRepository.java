package cn.tuyucheng.taketoday.books.repositories;

import org.springframework.data.repository.CrudRepository;

import cn.tuyucheng.taketoday.books.models.Library;

public interface LibraryRepository extends CrudRepository<Library, Long> {

}