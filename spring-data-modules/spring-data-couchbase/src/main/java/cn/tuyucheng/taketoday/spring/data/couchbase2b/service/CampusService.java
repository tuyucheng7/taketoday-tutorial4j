package cn.tuyucheng.taketoday.spring.data.couchbase2b.service;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Campus;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.util.Optional;
import java.util.Set;

public interface CampusService {

   Optional<Campus> find(String id);

   Set<Campus> findByName(String name);

   Set<Campus> findByLocationNear(Point point, Distance distance);

   Set<Campus> findAll();

   void save(Campus campus);
}