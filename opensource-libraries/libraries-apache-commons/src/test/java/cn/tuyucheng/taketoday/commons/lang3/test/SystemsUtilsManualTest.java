package cn.tuyucheng.taketoday.commons.lang3.test;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemsUtilsManualTest {

	// the paths depend on the OS and installed version of Java

	@Test
	public void givenSystemUtilsClass_whenCalledgetJavaHome_thenCorrect() {
		assertThat(SystemUtils.getJavaHome()).isEqualTo(new File("/usr/lib/jvm/java-8-oracle/jre"));
	}

	@Test
	public void givenSystemUtilsClass_whenCalledgetUserHome_thenCorrect() {
		assertThat(SystemUtils.getUserHome()).isEqualTo(new File("/home/travis"));
	}

	@Test
	public void givenSystemUtilsClass_whenCalledisJavaVersionAtLeast_thenCorrect() {
		assertThat(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_RECENT)).isTrue();
	}
}
