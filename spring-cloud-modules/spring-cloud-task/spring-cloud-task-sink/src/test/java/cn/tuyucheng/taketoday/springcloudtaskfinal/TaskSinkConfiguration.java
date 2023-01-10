package cn.tuyucheng.taketoday.springcloudtaskfinal;

import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;


@Configuration
public class TaskSinkConfiguration {

    @Bean
    public TaskLauncher taskLauncher() {
        return mock(TaskLauncher.class);
    }

}