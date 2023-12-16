package cn.tuyucheng.taketoday.namingstrategy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}