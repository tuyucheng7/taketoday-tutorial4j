package cn.tuyucheng.taketoday.spring.cloud.stream.rabbit;

import cn.tuyucheng.taketoday.spring.cloud.stream.rabbit.messages.TextPlainMessageConverter;
import cn.tuyucheng.taketoday.spring.cloud.stream.rabbit.model.LogMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding(Processor.class)
public class MyLoggerServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyLoggerServiceApplication.class, args);
	}

	@Bean
	public MessageConverter providesTextPlainMessageConverter() {
		return new TextPlainMessageConverter();
	}

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public LogMessage enrichLogMessage(LogMessage log) {
		return new LogMessage(String.format("[1]: %s", log.getMessage()));
	}
}
