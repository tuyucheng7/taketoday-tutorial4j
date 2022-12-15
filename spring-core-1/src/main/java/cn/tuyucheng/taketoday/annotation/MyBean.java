package cn.tuyucheng.taketoday.annotation;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@ExcludeFromJacocoGeneratedReport
public class MyBean {

	private final TestBean testBean;

	public MyBean(TestBean testBean) {
		this.testBean = testBean;
		System.out.println("Hello from constructor");
	}

	@PostConstruct
	private void postConstruct() {
		System.out.println("Hello from @PostConstruct method");
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("Bean is being destroyed");
	}
}