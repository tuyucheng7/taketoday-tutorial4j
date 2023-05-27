package cn.tuyucheng.taketoday.jwt.api;

import cn.tuyucheng.taketoday.jwt.resource.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PaymentController {

   @GetMapping("/payments")
   public List<Payment> getPayments() {
      List<Payment> payments = new ArrayList<>();
      for (int i = 1; i < 6; i++) {
         Payment payment = new Payment();
         payment.setId(String.valueOf(i));
         payment.setAmount(2);
         payments.add(payment);
      }
      return payments;
   }

}

