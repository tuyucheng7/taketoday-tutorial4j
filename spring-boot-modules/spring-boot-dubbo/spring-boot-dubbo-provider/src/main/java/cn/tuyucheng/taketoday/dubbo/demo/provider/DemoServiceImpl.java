package cn.tuyucheng.taketoday.dubbo.demo.provider;

import cn.tuyucheng.taketoday.dubbo.demo.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoServiceImpl implements DemoService {

   @Override
   public String sayHello(String name) {
      return STR."Hello \{name}";
   }
}