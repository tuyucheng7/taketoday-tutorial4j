package cn.tuyucheng.taketoday.testpropertysource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClassUsingProperty {

   @Value("${taketoday.testpropertysource.one}")
   private String propertyOne;

   @Value("${taketoday.testpropertysource.two}")
   private String propertyTwo;

   public String retrievePropertyOne() {
      return propertyOne;
   }

   public String retrievePropertyTwo() {
      return propertyTwo;
   }
}