package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.SpringDataRepositoryApplication;
import cn.tuyucheng.taketoday.boot.domain.MerchandiseEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRepositoryApplication.class)
class InventoryRepositoryIntegrationTest {

	private static final String ORIGINAL_TITLE = "Pair of Pants";
	private static final String UPDATED_TITLE = "Branded Luxury Pants";
	private static final String UPDATED_BRAND = "Armani";
	private static final String ORIGINAL_SHORTS_TITLE = "Pair of Shorts";

	@Autowired
	private InventoryRepository repository;

	@Test
	void shouldCreateNewEntryInDB() {
		MerchandiseEntity pants = new MerchandiseEntity(ORIGINAL_TITLE, BigDecimal.ONE);
		pants = repository.save(pants);

		MerchandiseEntity shorts = new MerchandiseEntity(ORIGINAL_SHORTS_TITLE, new BigDecimal(3));
		shorts = repository.save(shorts);

		assertNotNull(pants.getId());
		assertNotNull(shorts.getId());
		assertNotEquals(pants.getId(), shorts.getId());
	}

	@Test
	void shouldUpdateExistingEntryInDB() {
		MerchandiseEntity pants = new MerchandiseEntity(ORIGINAL_TITLE, BigDecimal.ONE);
		pants = repository.save(pants);

		Long originalId = pants.getId();

		pants.setTitle(UPDATED_TITLE);
		pants.setPrice(BigDecimal.TEN);
		pants.setBrand(UPDATED_BRAND);

		MerchandiseEntity result = repository.save(pants);

		assertEquals(originalId, result.getId());
		assertEquals(UPDATED_TITLE, result.getTitle());
		assertEquals(BigDecimal.TEN, result.getPrice());
		assertEquals(UPDATED_BRAND, result.getBrand());
	}

	@Test
	@Transactional
	void shouldUpdateExistingEntryInDBWithoutSave() {
		MerchandiseEntity pants = new MerchandiseEntity(ORIGINAL_TITLE, BigDecimal.ONE);
		pants = repository.save(pants);

		Long originalId = pants.getId();

		// Update using setters
		pants.setTitle(UPDATED_TITLE);
		pants.setPrice(BigDecimal.TEN);
		pants.setBrand(UPDATED_BRAND);

		Optional<MerchandiseEntity> resultOp = repository.findById(originalId);

		assertTrue(resultOp.isPresent());
		MerchandiseEntity result = resultOp.get();

		assertEquals(originalId, result.getId());
		assertEquals(UPDATED_TITLE, result.getTitle());
		assertEquals(BigDecimal.TEN, result.getPrice());
		assertEquals(UPDATED_BRAND, result.getBrand());
	}
}