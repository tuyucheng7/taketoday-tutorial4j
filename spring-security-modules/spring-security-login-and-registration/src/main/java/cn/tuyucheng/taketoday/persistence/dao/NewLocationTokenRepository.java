package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.NewLocationToken;
import cn.tuyucheng.taketoday.persistence.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

	NewLocationToken findByToken(String token);

	NewLocationToken findByUserLocation(UserLocation userLocation);

}
