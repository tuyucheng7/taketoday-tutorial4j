package cn.tuyucheng.taketoday.singleton;

import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@RequestScope
public class CarServiceBean {

   private UUID id = UUID.randomUUID();

   public UUID getId() {
      return this.id;
   }

   @Override
   public String toString() {
      return "CarService [id=" + id + "]";
   }

}
