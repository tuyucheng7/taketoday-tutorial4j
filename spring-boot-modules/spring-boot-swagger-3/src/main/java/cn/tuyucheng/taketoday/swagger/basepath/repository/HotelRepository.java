package cn.tuyucheng.taketoday.swagger.basepath.repository;

import cn.tuyucheng.taketoday.swagger.basepath.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}