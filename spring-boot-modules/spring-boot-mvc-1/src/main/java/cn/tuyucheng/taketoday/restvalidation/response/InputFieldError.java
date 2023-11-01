package cn.tuyucheng.taketoday.restvalidation.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputFieldError {
   private String field;
   private String message;
}