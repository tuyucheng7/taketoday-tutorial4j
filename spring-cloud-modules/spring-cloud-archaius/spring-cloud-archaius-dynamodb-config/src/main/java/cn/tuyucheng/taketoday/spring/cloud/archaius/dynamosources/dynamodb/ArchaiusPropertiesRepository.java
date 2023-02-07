package cn.tuyucheng.taketoday.spring.cloud.archaius.dynamosources.dynamodb;

import org.springframework.data.repository.CrudRepository;

public interface ArchaiusPropertiesRepository extends CrudRepository<ArchaiusProperties, String> {
}