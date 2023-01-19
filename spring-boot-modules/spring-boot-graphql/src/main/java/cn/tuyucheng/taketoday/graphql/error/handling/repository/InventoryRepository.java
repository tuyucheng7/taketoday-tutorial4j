package cn.tuyucheng.taketoday.graphql.error.handling.repository;

import cn.tuyucheng.taketoday.graphql.error.handling.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Vehicle, String> {
}