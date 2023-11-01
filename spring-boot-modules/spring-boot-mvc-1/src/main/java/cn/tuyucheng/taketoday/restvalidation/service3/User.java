package cn.tuyucheng.taketoday.restvalidation.service3;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

   @FieldNotEmpty(message = "{validation.notEmpty}", field = "{field.personalEmail}")
   private String email;
}