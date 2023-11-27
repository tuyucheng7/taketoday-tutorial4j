package cn.tuyucheng.taketoday.kafka.streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WordCountProcessor {

   private static final Serde<String> STRING_SERDE = Serdes.String();

   @Autowired
   void buildPipeline(StreamsBuilder streamsBuilder) {
      KStream<String, String> messageStream = streamsBuilder
            .stream("input-topic", Consumed.with(STRING_SERDE, STRING_SERDE));

      KTable<String, Long> wordCounts = messageStream
            .mapValues((ValueMapper<String, String>) String::toLowerCase)
            .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
            .groupBy((_, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
            .count(Materialized.as("counts"));

      wordCounts.toStream().to("output-topic");
   }
}