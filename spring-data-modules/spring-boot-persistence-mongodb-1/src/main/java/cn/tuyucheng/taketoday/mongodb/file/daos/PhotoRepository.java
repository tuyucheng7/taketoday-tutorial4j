package cn.tuyucheng.taketoday.mongodb.file.daos;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.mongodb.file.models.Photo;

public interface PhotoRepository extends MongoRepository<Photo, String> {

}