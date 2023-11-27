package cn.tuyucheng.taketoday.requestheader;

import cn.tuyucheng.taketoday.requestheader.interceptor.OperatorHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuzzController {
   private final OperatorHolder operatorHolder;

   public BuzzController(OperatorHolder operatorHolder) {
      this.operatorHolder = operatorHolder;
   }

   @GetMapping("buzz")
   public String buzz() {
      return STR."hello, \{operatorHolder.getOperator()}";
   }
}