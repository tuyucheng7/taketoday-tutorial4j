package cn.tuyucheng.taketoday.boot.json.convertfile.dao;

import cn.tuyucheng.taketoday.boot.json.convertfile.data.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {

}