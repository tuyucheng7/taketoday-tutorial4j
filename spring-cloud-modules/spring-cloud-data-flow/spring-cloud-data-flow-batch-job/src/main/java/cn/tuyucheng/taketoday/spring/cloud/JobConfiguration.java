package cn.tuyucheng.taketoday.spring.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

   private static final Log logger = LogFactory.getLog(JobConfiguration.class);

   @Autowired
   public JobBuilderFactory jobBuilderFactory;

   @Autowired
   public StepBuilderFactory stepBuilderFactory;

   @Bean
   public Job job() {
      return jobBuilderFactory.get("job").start(stepBuilderFactory.get("jobStep1").tasklet((contribution, chunkContext) -> {
         logger.info("Job was run");
         return RepeatStatus.FINISHED;
      }).build()).build();
   }
}