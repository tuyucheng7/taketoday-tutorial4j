package cn.tuyucheng.taketoday.version;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = VersionObtainer.class)
public class VersionObtainerUnitTest {

	public VersionObtainer version = new VersionObtainer();

	@Test
	public void testGetSpringVersion() {
		String res = version.getSpringVersion();
		assertThat(res).isNotEmpty();
	}

	@Test
	public void testGetJdkVersion() {
		String res = version.getJdkVersion();
		assertThat(res).isNotEmpty();
	}

	@Test
	public void testGetJavaVersion() {
		String res = version.getJavaVersion();
		assertThat(res).isNotEmpty();
	}
}
