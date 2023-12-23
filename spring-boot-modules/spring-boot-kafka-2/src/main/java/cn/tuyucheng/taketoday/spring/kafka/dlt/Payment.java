package cn.tuyucheng.taketoday.spring.kafka.dlt;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.StringJoiner;

public class Payment {
   private String reference;
   private BigDecimal amount;
   private Currency currency;

   public String getReference() {
      return reference;
   }

   public void setReference(String reference) {
      this.reference = reference;
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
      return new StringJoiner(", ", STR."\{Payment.class.getSimpleName()}[", "]").add(STR."reference='\{reference}'")
            .add(STR."amount=\{amount}")
            .add(STR."currency=\{currency}")
            .toString();
   }
}