package cn.tuyucheng.taketoday.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<cn.tuyucheng.taketoday.boot.domain.Car, Integer> {

	boolean existsCarByPower(int power);

	boolean existsCarByModel(String model);

	@Query("select case when count(c)> 0 then true else false end from Car c where c.model = :model")
	boolean existsCarExactCustomQuery(@Param("model") String model);

	@Query("select case when count(c)> 0 then true else false end from Car c where lower(c.model) like lower(:model)")
	boolean existsCarLikeCustomQuery(@Param("model") String model);
}