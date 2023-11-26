package cn.tuyucheng.taketoday.restvalidation.service2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

   @NotEmpty(message = "{validation.email.notEmpty}")
   private String email;
}