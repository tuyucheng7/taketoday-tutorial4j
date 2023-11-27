package cn.tuyucheng.taketoday.spring.kafka.multipletopics;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.StringJoiner;

public class PaymentData {
   private String paymentReference;
   private String type;
   private BigDecimal amount;
   private Currency currency;

   public String getPaymentReference() {
      return paymentReference;
   }

   public void setPaymentReference(String paymentReference) {
      this.paymentReference = paymentReference;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public Currency getCurrency() {
      return currency;
   }

   public void setCurrency(Currency currency) {
      this.currency = currency;
   }

   @Override
   public String toString() {
      return new StringJoiner(", ", STR."\{PaymentData.class.getSimpleName()}[", "]")
            .add(STR."paymentReference='\{paymentReference}'")
            .add(STR."type='\{type}'")
            .add(STR."amount=\{amount}")
            .add(STR."currency=\{currency}")
            .toString();
   }
}