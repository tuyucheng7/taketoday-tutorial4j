package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.spring.drools.service.TaxiFareConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TaxiFareConfiguration.class)
public class SpringContextTest {

	@Test
	public void whenSpringContextIsBootstrapped_thenNoExceptions() {
	}
}