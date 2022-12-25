package cn.tuyucheng.taketoday.properties.parentchild;

import cn.tuyucheng.taketoday.properties.parentchild.config.ChildConfig;
import cn.tuyucheng.taketoday.properties.parentchild.config.ParentConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextHierarchy({@ContextConfiguration(classes = ParentConfig.class), @ContextConfiguration(classes = ChildConfig.class)})
class ParentChildPropertySourcePropertiesIntegrationTest {

	@Autowired
	private WebApplicationContext wac;

	@Test
	void givenPropertySource_whenGetPropertyUsingEnv_thenCorrect() {
		final Environment childEnv = wac.getEnvironment();
		final Environment parentEnv = wac.getParent().getEnvironment();

		assertEquals("parent", parentEnv.getProperty("parent.name"));
		assertNull(parentEnv.getProperty("child.name"));

		assertEquals("parent", childEnv.getProperty("parent.name"));
		assertEquals("child", childEnv.getProperty("child.name"));
	}

	@Test
	void givenPropertySource_whenGetPropertyUsingValueAnnotation_thenCorrect() {
		final ChildValueHolder childValueHolder = wac.getBean(ChildValueHolder.class);
		final ParentValueHolder parentValueHolder = wac.getParent().getBean(ParentValueHolder.class);

		assertEquals("parent", parentValueHolder.getParentName());
		assertEquals("-", parentValueHolder.getChildName());

		assertEquals("parent", childValueHolder.getParentName());
		assertEquals("child", childValueHolder.getChildName());
	}
}