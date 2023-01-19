package cn.tuyucheng.taketoday.concurrent.prioritytaskexecution;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Job implements Runnable {
	private final String jobName;
	private final JobPriority jobPriority;

	public Job(String jobName, JobPriority jobPriority) {
		this.jobName = jobName;
		this.jobPriority = jobPriority != null ? jobPriority : JobPriority.MEDIUM;
	}

	public JobPriority getJobPriority() {
		return jobPriority;
	}

	@Override
	public void run() {
		try {
			log.debug("Job:{} Priority:{}", jobName, jobPriority);
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {
		}
	}
}