package cn.tuyucheng.taketoday.caffeine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DataObject {
   private static final Logger log = LoggerFactory.getLogger(DataObject.class);
   private static int objectCounter = 0;
   private final String data;

   private DataObject(String data) {
      this.data = data;
   }

   public static DataObject get(String data) {
      objectCounter++;
      log.info("Init DataObject#{} with '{}'", objectCounter, data);
      return new DataObject(data);
   }

   public String getData() {
      return data;
   }

   @Override
   public String toString() {
      return "DataObject{" + "data='" + data + '\'' + '}';
   }
}