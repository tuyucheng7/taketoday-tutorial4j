package cn.tuyucheng.taketoday.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Very simple Person entity.
 * Use the Fluent-style interface to set properties.
 *
 * @author tuyucheng
 */
@Data
@Accessors(chain = true)
public class Person {

   private Long id;
   private String lastName;
   private String firstName;
}