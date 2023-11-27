package cn.tuyucheng.taketoday.spring.kafka.kafkasplitting;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.TopicNameExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.KafkaStreamBrancher;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaStreamsConfig {
   @Value("${kafka.topics.iot}")
   private String iotTopicName;

   @Bean
   public Serde<IotSensorData> iotSerde() {
      return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(IotSensorData.class));
   }

   @Bean
   public KStream<String, IotSensorData> iotStream(StreamsBuilder streamsBuilder) {
      KStream<String, IotSensorData> stream = streamsBuilder.stream(iotTopicName, Consumed.with(Serdes.String(), iotSerde()));
      stream.split()
            .branch((_, value) -> value.getSensorType() != null,
                  Branched.withConsumer(ks -> ks.to((_, value, _) -> String.format("%s_%s", iotTopicName, value.getSensorType()))))
            .noDefaultBranch();
      return stream;
   }

   @Bean
   public KStream<String, IotSensorData> iotBrancher(StreamsBuilder streamsBuilder) {
      KStream<String, IotSensorData> stream = streamsBuilder.stream(iotTopicName, Consumed.with(Serdes.String(), iotSerde()));

      new KafkaStreamBrancher<String, IotSensorData>()
            .branch((_, value) -> "temp".equals(value.getSensorType()), (ks) -> ks.to(STR."\{iotTopicName}_temp"))
            .branch((_, value) -> "move".equals(value.getSensorType()), (ks) -> ks.to(STR."\{iotTopicName}_move"))
            .branch((_, value) -> "hum".equals(value.getSensorType()), (ks) -> ks.to(STR."\{iotTopicName}_hum"))
            .defaultBranch(ks -> ks.to(String.format("%s_unknown", iotTopicName)))
            .onTopOf(stream);

      return stream;
   }

   @Bean
   public KStream<String, IotSensorData> iotTopicExtractor(StreamsBuilder streamsBuilder) {
      KStream<String, IotSensorData> stream = streamsBuilder.stream(iotTopicName, Consumed.with(Serdes.String(), iotSerde()));
      TopicNameExtractor<String, IotSensorData> sensorTopicExtractor = (_, value, _) -> String.format("%s_%s", iotTopicName, value.getSensorType());
      stream.to(sensorTopicExtractor);
      return stream;
   }
}