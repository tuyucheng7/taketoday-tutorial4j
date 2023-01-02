package cn.tuyucheng.taketoday.maven.it;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FailsafeBuildPhaseIntegrationTest {

	@Test
	void whenTestExecutes_thenPreAndPostIntegrationBuildPhasesAreExecuted() {
		assertTrue(true);
	}
}