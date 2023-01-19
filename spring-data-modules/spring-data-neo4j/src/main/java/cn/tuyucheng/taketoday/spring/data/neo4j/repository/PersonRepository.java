package cn.tuyucheng.taketoday.spring.data.neo4j.repository;

import cn.tuyucheng.taketoday.spring.data.neo4j.domain.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {
}
