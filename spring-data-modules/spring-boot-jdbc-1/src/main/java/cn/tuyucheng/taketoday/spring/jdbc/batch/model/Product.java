package cn.tuyucheng.taketoday.spring.jdbc.batch.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
   private long id;
   private String title;
   private LocalDateTime createdTs;
   private BigDecimal price;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public LocalDateTime getCreatedTs() {
      return createdTs;
   }

   public void setCreatedTs(LocalDateTime createdTs) {
      this.createdTs = createdTs;
   }

   public BigDecimal getPrice() {
      return price;
   }

   public void setPrice(BigDecimal price) {
      this.price = price;
   }

   @Override
   public String toString() {
      return "Product{" + "id=" + id +
            ", title='" + title + '\'' +
            ", createdTs=" + createdTs +
            ", price=" + price +
            '}';
   }
}