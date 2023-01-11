package cn.tuyucheng.taketoday.attribute.override.repository;

import cn.tuyucheng.taketoday.attribute.override.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Integer> {
}