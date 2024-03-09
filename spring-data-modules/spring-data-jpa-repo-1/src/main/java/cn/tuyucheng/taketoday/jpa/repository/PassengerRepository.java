package cn.tuyucheng.taketoday.jpa.repository;

import cn.tuyucheng.taketoday.jpa.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

   List<Passenger> findByFirstNameIgnoreCase(String firstName);
}