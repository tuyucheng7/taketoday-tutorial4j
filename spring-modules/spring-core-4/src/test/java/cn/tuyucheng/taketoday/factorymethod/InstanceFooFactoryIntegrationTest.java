package cn.tuyucheng.taketoday.factorymethod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/factorymethod/instance-foo-config.xml")
public class InstanceFooFactoryIntegrationTest {

	@Autowired
	private Foo foo;

	@Test
	public void givenValidInstanceFactoryConfig_whenCreateFooInstance_thenInstanceIsNotNull() {
		assertNotNull(foo);
	}
}
