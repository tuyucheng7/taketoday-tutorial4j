package cn.tuyucheng.taketoday.testcontainers.middleearth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("characters")
public record MiddleEarthCharacter(@Id String id, String name, String race) {
   public MiddleEarthCharacter(String name, String race) {
      this(UUID.randomUUID().toString(), name, race);
   }
}