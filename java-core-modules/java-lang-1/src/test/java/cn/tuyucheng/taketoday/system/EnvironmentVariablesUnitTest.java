package cn.tuyucheng.taketoday.system;

import org.junit.Assert;
import org.junit.Test;

public class EnvironmentVariablesUnitTest {

	@Test
	public void givenEnvVars_whenReadPath_thenGetValueinResult() {
		EnvironmentVariables environmentVariables = new EnvironmentVariables();

		Assert.assertNotNull(environmentVariables.getPath());
	}
}
