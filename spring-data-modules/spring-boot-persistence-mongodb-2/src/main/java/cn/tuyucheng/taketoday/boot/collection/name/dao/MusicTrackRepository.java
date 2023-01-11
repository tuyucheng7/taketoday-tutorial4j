package cn.tuyucheng.taketoday.boot.collection.name.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.tuyucheng.taketoday.boot.collection.name.data.MusicTrack;

public interface MusicTrackRepository extends MongoRepository<MusicTrack, String> {

}