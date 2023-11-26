package cn.tuyucheng.taketoday.dockercompose.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

   @Id
   private String id;
   private String name;
   private int quantity;
   private String category;
}