package cn.tuyucheng.taketoday.h2db.notnull;

import cn.tuyucheng.taketoday.h2db.notnull.daos.ItemRepository;
import cn.tuyucheng.taketoday.h2db.notnull.models.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NotNullVsNullableApplication.class)
class ItemIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void shouldNotAllowToPersistNullItemsPrice() {
		assertThatThrownBy(() -> itemRepository.save(new Item()))
			.hasRootCauseInstanceOf(ConstraintViolationException.class)
			.hasStackTraceContaining("must not be null");
	}
}