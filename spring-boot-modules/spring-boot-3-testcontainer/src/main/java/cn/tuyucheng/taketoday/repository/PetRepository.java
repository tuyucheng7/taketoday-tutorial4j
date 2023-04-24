package cn.tuyucheng.taketoday.repository;

import cn.tuyucheng.taketoday.entity.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PetRepository extends MongoRepository<Pet, String> {
}