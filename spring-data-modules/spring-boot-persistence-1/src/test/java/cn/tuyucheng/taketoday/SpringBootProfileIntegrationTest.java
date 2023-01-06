package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.boot.Application;
import cn.tuyucheng.taketoday.boot.domain.GenericEntity;
import cn.tuyucheng.taketoday.boot.repository.GenericEntityRepository;
import cn.tuyucheng.taketoday.config.H2TestProfileJPAConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class, H2TestProfileJPAConfig.class})
@ActiveProfiles("test")
class SpringBootProfileIntegrationTest {
	@Autowired
	private GenericEntityRepository genericEntityRepository;

	@Test
	void givenGenericEntityRepository_whenSaveAndRetrieveEntity_thenOK() {
		GenericEntity genericEntity = genericEntityRepository.save(new GenericEntity("test"));
		GenericEntity foundEntity = genericEntityRepository.findById(genericEntity.getId()).orElse(null);

		assertNotNull(foundEntity);
		assertEquals(genericEntity.getValue(), foundEntity.getValue());
	}
}