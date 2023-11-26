package cn.tuyucheng.taketoday.testcontainers.middleearth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiddleEarthCharactersRepository extends MongoRepository<MiddleEarthCharacter, String> {
   List<MiddleEarthCharacter> findAllByRace(String race);
}