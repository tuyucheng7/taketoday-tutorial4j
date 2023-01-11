package cn.tuyucheng.taketoday.entitygraph.repository;

import cn.tuyucheng.taketoday.entitygraph.model.Characteristic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacteristicsRepository extends JpaRepository<Characteristic, Long> {

	@EntityGraph(attributePaths = {"item"})
	Characteristic findByType(String type);
}