package cn.tuyucheng.taketoday.boot.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.tuyucheng.taketoday.boot.domain.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}