package cn.tuyucheng.taketoday.guice.service.impl;

import cn.tuyucheng.taketoday.guice.service.DataPumpService;

import java.util.UUID;

public class DataPumpServiceImpl implements DataPumpService {

   @Override
   public String generate() {
      return UUID.randomUUID().toString();
   }

}
