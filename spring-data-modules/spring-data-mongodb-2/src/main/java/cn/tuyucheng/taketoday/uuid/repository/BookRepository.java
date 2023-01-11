package cn.tuyucheng.taketoday.uuid.repository;

import cn.tuyucheng.taketoday.uuid.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BookRepository extends MongoRepository<Book, UUID> {

}
