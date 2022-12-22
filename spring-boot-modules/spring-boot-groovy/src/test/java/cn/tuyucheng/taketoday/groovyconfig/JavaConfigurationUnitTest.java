package cn.tuyucheng.taketoday.groovyconfig;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaConfigurationUnitTest {

	@Test
	void whenJavaConfig_thenCorrectPerson() {

		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(JavaBeanConfig.class);
		ctx.refresh();

		JavaPersonBean j = ctx.getBean(JavaPersonBean.class);

		assertEquals("31", j.getAge());
		assertEquals("green", j.getEyesColor());
		assertEquals("blond", j.getHairColor());
	}
}