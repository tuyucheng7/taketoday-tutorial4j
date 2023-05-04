package cn.tuyucheng.taketoday.spring.data.cassandra.test.repository;

import cn.tuyucheng.taketoday.spring.data.cassandra.test.domain.Vehicle;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends CrudRepository<Vehicle, String> {

   @Query("select * from vehicles")
   List<Vehicle> findAllVehicles();

   Optional<Vehicle> findByVin(@Param("vin") String vin);

   void deleteByVin(String vin);
}