package cn.tuyucheng.taketoday.wiring.configuration.inject;

import cn.tuyucheng.taketoday.dependency.ArbitraryDependency;
import cn.tuyucheng.taketoday.wiring.configuration.ApplicationContextTestInjectName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import javax.inject.Named;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
	loader = AnnotationConfigContextLoader.class,
	classes = ApplicationContextTestInjectName.class)
public class FieldByNameInjectIntegrationTest {

	@Inject
	@Named("yetAnotherFieldInjectDependency")
	private ArbitraryDependency yetAnotherFieldInjectDependency;

	@Test
	public void givenInjectQualifier_WhenSetOnField_ThenDependencyValid() {
		assertNotNull(yetAnotherFieldInjectDependency);
		assertEquals("Yet Another Arbitrary Dependency", yetAnotherFieldInjectDependency.toString());
	}
}
