package cn.tuyucheng.taketoday.caching.ttl.repository;

import cn.tuyucheng.taketoday.caching.ttl.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}