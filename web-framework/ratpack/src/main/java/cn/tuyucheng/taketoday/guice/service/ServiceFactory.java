package cn.tuyucheng.taketoday.guice.service;

import cn.tuyucheng.taketoday.guice.service.impl.DataPumpServiceImpl;

public class ServiceFactory {

   private static DataPumpService instance;

   public static DataPumpService getInstance() {
      if (instance == null) {
         return new DataPumpServiceImpl();
      }
      return instance;
   }

   public static void setInstance(DataPumpService dataPumpService) {
      instance = dataPumpService;
   }

}
