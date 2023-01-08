package cn.tuyucheng.taketoday.springamqp.errorhandling.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
	value = "amqp.configuration.current",
	havingValue = "routing-dlq")
public class RoutingKeyDLQAmqpConfiguration {
	public static final String DLX_EXCHANGE_MESSAGES = SimpleDLQAmqpConfiguration.QUEUE_MESSAGES + ".dlx";

	@Bean
	Queue messagesQueue() {
		return QueueBuilder.durable(SimpleDLQAmqpConfiguration.QUEUE_MESSAGES)
			.withArgument("x-dead-letter-exchange", DLX_EXCHANGE_MESSAGES)
			.build();
	}

	@Bean
	FanoutExchange deadLetterExchange() {
		return new FanoutExchange(DLX_EXCHANGE_MESSAGES);
	}

	@Bean
	Queue deadLetterQueue() {
		return QueueBuilder.durable(SimpleDLQAmqpConfiguration.QUEUE_MESSAGES_DLQ).build();
	}

	@Bean
	Binding deadLetterBinding() {
		return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
	}

	@Bean
	DirectExchange messagesExchange() {
		return new DirectExchange(SimpleDLQAmqpConfiguration.EXCHANGE_MESSAGES);
	}

	@Bean
	Binding bindingMessages() {
		return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(SimpleDLQAmqpConfiguration.QUEUE_MESSAGES);
	}
}
