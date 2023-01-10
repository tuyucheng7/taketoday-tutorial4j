package cn.tuyucheng.taketoday.twitterhdfs.aggregate;

import cn.tuyucheng.taketoday.twitterhdfs.processor.ProcessorApp;
import cn.tuyucheng.taketoday.twitterhdfs.sink.SinkApp;
import cn.tuyucheng.taketoday.twitterhdfs.source.SourceApp;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder;

@SpringBootApplication
public class AggregateApp {
    public static void main(String[] args) {
        new AggregateApplicationBuilder()
              .from(SourceApp.class).args("--fixedDelay=5000")
              .via(ProcessorApp.class)
              .to(SinkApp.class).args("--debug=true")
              .run(args);
    }
}