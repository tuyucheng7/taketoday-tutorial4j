package cn.tuyucheng.taketoday.task;

import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;

import javax.sql.DataSource;

public class HelloWorldTaskConfigurer
      extends
      DefaultTaskConfigurer {

    public HelloWorldTaskConfigurer(DataSource dataSource) {
        super(dataSource);
    }
}