package cn.tuyucheng.taketoday.properties.value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:values.properties")
public record PriorityRecord(@Value("${priority:normal}") String priority) {

   @Autowired
   public PriorityRecord(@Value("${priority:normal}") String priority) {
      this.priority = priority;
   }
}