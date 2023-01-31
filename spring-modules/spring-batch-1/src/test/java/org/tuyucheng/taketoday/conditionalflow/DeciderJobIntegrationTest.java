package org.tuyucheng.taketoday.conditionalflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.tuyucheng.taketoday.conditionalflow.config.NumberInfoConfig;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {NumberInfoConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DeciderJobIntegrationTest {
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	void givenNumberGeneratorDecider_whenDeciderRuns_thenStatusIsNotify() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
		ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

		assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
		assertEquals(2, actualStepExecutions.size());
		boolean notifyStepDidRun = false;
		Iterator<StepExecution> iterator = actualStepExecutions.iterator();
		while (iterator.hasNext() && !notifyStepDidRun) {
			if (iterator.next()
				.getStepName()
				.equals("Notify step")) {
				notifyStepDidRun = true;
			}
		}
		assertTrue(notifyStepDidRun);
	}
}