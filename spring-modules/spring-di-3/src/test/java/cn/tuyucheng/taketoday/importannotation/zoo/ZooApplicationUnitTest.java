package cn.tuyucheng.taketoday.importannotation.zoo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ZooApplication.class)
class ZooApplicationUnitTest {

	@Autowired
	ApplicationContext context;

	@Test
	void givenTheScanInTheAnimalPackage_whenGettingAnyAnimal_shallFindItInTheContext() {
		assertNotNull(context.getBean("dog"));
		assertNotNull(context.getBean("bird"));
		assertNotNull(context.getBean("cat"));
		assertNotNull(context.getBean("bug"));
	}
}
