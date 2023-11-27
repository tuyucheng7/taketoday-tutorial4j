package cn.tuyucheng.taketoday.jsondateformat;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class ContactAppConfig {

   private static final String dateFormat = "yyyy-MM-dd";

   private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

   @Bean
   @ConditionalOnProperty(value = "spring.jackson.date-format", matchIfMissing = true, havingValue = "none")
   public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
      return builder -> {
         builder.simpleDateFormat(dateTimeFormat);
         builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
         builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
      };
   }
}