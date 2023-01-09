package cn.tuyucheng.taketoday.flink.connector;

import cn.tuyucheng.taketoday.flink.model.Backup;
import cn.tuyucheng.taketoday.flink.schema.BackupSerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

public class Producers {

	public static FlinkKafkaProducer011<String> createStringProducer(String topic, String kafkaAddress) {
		return new FlinkKafkaProducer011<>(kafkaAddress, topic, new SimpleStringSchema());
	}

	public static FlinkKafkaProducer011<Backup> createBackupProducer(String topic, String kafkaAddress) {
		return new FlinkKafkaProducer011<Backup>(kafkaAddress, topic, new BackupSerializationSchema());
	}
}
