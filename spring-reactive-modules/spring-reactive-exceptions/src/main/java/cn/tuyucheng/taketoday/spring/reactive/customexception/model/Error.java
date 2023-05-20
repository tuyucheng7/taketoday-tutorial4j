package cn.tuyucheng.taketoday.spring.reactive.customexception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {
   protected String reference;
   private int code;
   private String message;
}