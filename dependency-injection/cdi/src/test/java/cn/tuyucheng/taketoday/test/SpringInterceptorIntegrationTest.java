package cn.tuyucheng.taketoday.test;

import cn.tuyucheng.taketoday.spring.configuration.AppConfig;
import cn.tuyucheng.taketoday.spring.service.SpringSuperService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@Ignore
@ContextConfiguration(classes = {AppConfig.class})
public class SpringInterceptorIntegrationTest {
	@Autowired
	SpringSuperService springSuperService;

	@Autowired
	private List<String> accumulator;

	//

	@Test
	public void givenService_whenServiceAndAspectExecuted_thenOk() {
		String code = "123456";
		String result = springSuperService.getInfoFromService(code);

		Assert.assertThat(accumulator.size(), is(2));
		Assert.assertThat(accumulator.get(0), is("Call to getInfoFromService"));
		Assert.assertThat(accumulator.get(1), is("Method called successfully: getInfoFromService"));
	}

}
