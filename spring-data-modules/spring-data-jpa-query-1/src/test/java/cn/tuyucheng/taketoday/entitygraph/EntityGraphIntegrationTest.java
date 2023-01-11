package cn.tuyucheng.taketoday.entitygraph;

import cn.tuyucheng.taketoday.entitygraph.model.Characteristic;
import cn.tuyucheng.taketoday.entitygraph.model.Item;
import cn.tuyucheng.taketoday.entitygraph.repository.CharacteristicsRepository;
import cn.tuyucheng.taketoday.entitygraph.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@ExtendWith(SpringExtension.class)
@Sql(scripts = "/entitygraph-data.sql")
class EntityGraphIntegrationTest {

	@Autowired
	private ItemRepository itemRepo;

	@Autowired
	private CharacteristicsRepository characteristicsRepo;

	@Test
	void givenEntityGraph_whenCalled_shouldReturnDefinedFields() {
		Item item = itemRepo.findByName("Table");
		assertThat(item.getId()).isEqualTo(1L);
	}

	@Test
	void givenAdhocEntityGraph_whenCalled_shouldReturnDefinedFields() {
		Characteristic characteristic = characteristicsRepo.findByType("Rigid");
		assertThat(characteristic.getId()).isEqualTo(1L);
	}
}