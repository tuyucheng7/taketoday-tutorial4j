package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}