package cn.tuyucheng.taketoday.spring.jdbc.autogenkey;

import cn.tuyucheng.taketoday.spring.jdbc.autogenkey.repository.MessageRepositoryJDBCTemplate;
import cn.tuyucheng.taketoday.spring.jdbc.autogenkey.repository.MessageRepositorySimpleJDBCInsert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class GetAutoGenKeyByJDBC {

   @Configuration
   @ComponentScan(basePackages = {"cn.tuyucheng.taketoday.spring.jdbc.autogenkey"})
   public static class SpringConfig {
   }

   @Autowired
   MessageRepositorySimpleJDBCInsert messageRepositorySimpleJDBCInsert;

   @Autowired
   MessageRepositoryJDBCTemplate messageRepositoryJDBCTemplate;

   final String MESSAGE_CONTENT = "Test";

   @Test
   void insertJDBC_whenLoadMessageByKey_thenGetTheSameMessage() {
      long key = messageRepositoryJDBCTemplate.insert(MESSAGE_CONTENT);
      String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);

      assertEquals(MESSAGE_CONTENT, loadedMessage);
   }

   @Test
   void insertSimpleInsert_whenLoadMessageKey_thenGetTheSameMessage() {
      long key = messageRepositorySimpleJDBCInsert.insert(MESSAGE_CONTENT);
      String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);

      assertEquals(MESSAGE_CONTENT, loadedMessage);
   }
}