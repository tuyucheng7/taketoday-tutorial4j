package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.Location;
import org.springframework.data.repository.Repository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ReadOnlyLocationRepository extends Repository<Location, Long> {

	Optional<Location> findById(Long id);

	Location save(Location location);
}