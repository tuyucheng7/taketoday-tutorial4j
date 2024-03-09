package cn.tuyucheng.taketoday.spring.data.persistence.json;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address {

   private String postCode;

   private String city;
}