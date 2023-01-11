package cn.tuyucheng.taketoday.boot.collection.name.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.boot.collection.name.data.MusicAlbum;

public interface MusicAlbumRepository extends MongoRepository<MusicAlbum, String> {

}