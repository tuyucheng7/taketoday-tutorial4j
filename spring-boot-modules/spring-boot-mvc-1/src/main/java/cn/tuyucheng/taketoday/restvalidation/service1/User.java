package cn.tuyucheng.taketoday.restvalidation.service1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

   @NotEmpty
   private String email;
}