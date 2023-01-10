package cn.tuyucheng.taketoday.kclkpl;

import com.amazonaws.services.kinesis.producer.KinesisProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

@Component
public class IpProducer {

	@Value("${ips.stream}")
	private String IPS_STREAM;

	@Value("${ips.partition.key}")
	private String IPS_PARTITION_KEY;

	@Autowired
	private KinesisProducer kinesisProducer;

	@Scheduled(fixedDelay = 3000L)
	private void produce() {
		IntStream.range(1, 200).mapToObj(ipSuffix -> ByteBuffer.wrap(("192.168.0." + ipSuffix).getBytes()))
			.forEach(entry -> kinesisProducer.addUserRecord(IPS_STREAM, IPS_PARTITION_KEY, entry));
	}
}