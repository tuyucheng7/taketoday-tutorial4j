package cn.tuyucheng.taketoday.persistence;

import cn.tuyucheng.taketoday.model.Foo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
public class DataSetupBean implements InitializingBean {

	@Autowired
	private FooRepository repo;

	@Override
	public void afterPropertiesSet() throws Exception {
		IntStream.range(1, 5).forEach(i -> repo.save(new Foo(randomAlphabetic(8))));
	}
}