package cn.tuyucheng.taketoday.swagger.basepath.repository;

import cn.tuyucheng.taketoday.swagger.basepath.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}