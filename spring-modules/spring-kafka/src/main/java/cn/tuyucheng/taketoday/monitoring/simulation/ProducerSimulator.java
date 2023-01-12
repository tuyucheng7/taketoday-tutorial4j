package cn.tuyucheng.taketoday.monitoring.simulation;

import cn.tuyucheng.taketoday.monitoring.util.MonitoringUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class ProducerSimulator {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final String topicName;
	private final boolean enabled;

	@Autowired
	public ProducerSimulator(
		KafkaTemplate<String, String> kafkaTemplate,
		@Value(value = "${monitor.topic.name}") String topicName,
		@Value(value = "${monitor.producer.simulate}") String enabled) {
		this.kafkaTemplate = kafkaTemplate;
		this.topicName = topicName;
		this.enabled = BooleanUtils.toBoolean(enabled);
	}

	@Scheduled(fixedDelay = 1L, initialDelay = 5L)
	public void sendMessage() throws ExecutionException, InterruptedException {
		if (enabled) {
			if (MonitoringUtil.endTime.after(new Date())) {
				String message = "msg-" + MonitoringUtil.time();
				SendResult<String, String> result = kafkaTemplate.send(topicName, message).get();
			}
		}
	}
}
