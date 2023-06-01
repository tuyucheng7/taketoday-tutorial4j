package cn.tuyucheng.taketoday.properties.value;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PriorityRecord.class)
class PriorityRecordIntegrationTest {

   @Autowired
   private PriorityRecord priorityRecord;

   @Test
   void givenPropertyFile_WhenConstructorInjectionUsedInRecord_ThenValueInjected() {
      assertThat(priorityRecord.priority()).isEqualTo("high");
   }
}