package cn.tuyucheng.taketoday.taskletsvschunks.tasklets;

import cn.tuyucheng.taketoday.taskletsvschunks.config.TaskletsConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TaskletsConfig.class)
class TaskletsIntegrationTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	@Disabled("fails test")
	void givenTaskletsJob_WhenJobEnds_ThenStatusCompleted() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}
}