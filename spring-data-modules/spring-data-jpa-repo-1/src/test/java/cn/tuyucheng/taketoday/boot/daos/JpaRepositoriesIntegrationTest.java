package cn.tuyucheng.taketoday.boot.daos;

import cn.tuyucheng.taketoday.boot.domain.Item;
import cn.tuyucheng.taketoday.boot.domain.ItemType;
import cn.tuyucheng.taketoday.boot.domain.Location;
import cn.tuyucheng.taketoday.boot.domain.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = "spring.sql.init.data-locations=classpath:import_entities.sql", showSql = false)
class JpaRepositoriesIntegrationTest {
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private ItemTypeRepository compositeRepository;
	@Autowired
	private ReadOnlyLocationRepository readOnlyRepository;

	@Test
	void whenSaveLocation_ThenGetSameLocation() {
		Location location = new Location();
		location.setId(100L);
		location.setCountry("Country H");
		location.setCity("City Hundred");
		location = locationRepository.saveAndFlush(location);

		Location otherLocation = locationRepository.getOne(location.getId());
		assertEquals("Country H", otherLocation.getCountry());
		assertEquals("City Hundred", otherLocation.getCity());

		locationRepository.delete(otherLocation);
	}

	@Test
	void givenLocationId_whenFindStores_thenGetStores() {
		List<Store> stores = storeRepository.findStoreByLocationId(1L);
		assertEquals(1, stores.size());
	}

	@Test
	void givenItemTypeId_whenDeleted_ThenItemTypeDeleted() {
		Optional<ItemType> itemType = compositeRepository.findById(1L);
		assertTrue(itemType.isPresent());
		compositeRepository.deleteCustom(itemType.get());
		itemType = compositeRepository.findById(1L);
		assertFalse(itemType.isPresent());
	}

	@Test
	void givenItemId_whenUsingCustomRepo_ThenDeleteAppropriateEntity() {
		Item item = compositeRepository.findItemById(1L);
		assertNotNull(item);
		compositeRepository.deleteCustom(item);
		item = compositeRepository.findItemById(1L);
		assertNull(item);
	}

	@Test
	void givenItemAndItemType_WhenAmbiguousDeleteCalled_ThenItemTypeDeletedAndNotItem() {
		Optional<ItemType> itemType = compositeRepository.findById(1L);
		assertTrue(itemType.isPresent());
		Item item = compositeRepository.findItemById(2L);
		assertNotNull(item);

		compositeRepository.findThenDelete(1L);
		Optional<ItemType> sameItemType = compositeRepository.findById(1L);
		assertFalse(sameItemType.isPresent());
		Item sameItem = compositeRepository.findItemById(2L);
		assertNotNull(sameItem);
	}

	@Test
	void whenCreatingReadOnlyRepo_thenHaveOnlyReadOnlyOperationsAvailable() {
		Optional<Location> location = readOnlyRepository.findById(1L);
		assertNotNull(location);
	}
}