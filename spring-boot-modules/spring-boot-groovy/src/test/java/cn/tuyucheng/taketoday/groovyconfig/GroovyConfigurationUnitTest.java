package cn.tuyucheng.taketoday.groovyconfig;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericGroovyApplicationContext;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyConfigurationUnitTest {

	private static final String FILE_NAME = "GroovyBeanConfig.groovy";
	private static final String FILE_PATH = "src/main/java/cn/tuyucheng/taketoday/groovyconfig/";

	@Test
	void whenGroovyConfig_thenCorrectPerson() throws Exception {
		GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext();
		ctx.load("file:" + getPathPart() + FILE_NAME);
		ctx.refresh();

		JavaPersonBean j = ctx.getBean(JavaPersonBean.class);

		assertEquals("32", j.getAge());
		assertEquals("blue", j.getEyesColor());
		assertEquals("black", j.getHairColor());
	}

	@Test
	void whenGroovyConfig_thenCorrectListLength() throws Exception {
		GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext();
		ctx.load("file:" + getPathPart() + FILE_NAME);
		ctx.refresh();

		BandsBean bb = ctx.getBean(BandsBean.class);

		assertEquals(3, bb.getBandsList().size());
	}

	private String getPathPart() {
		String pathPart = new File(".").getAbsolutePath();
		pathPart = pathPart.replace(".", "");
		pathPart = pathPart.replace("\\", "/");
		pathPart = pathPart + FILE_PATH;

		return pathPart;
	}
}