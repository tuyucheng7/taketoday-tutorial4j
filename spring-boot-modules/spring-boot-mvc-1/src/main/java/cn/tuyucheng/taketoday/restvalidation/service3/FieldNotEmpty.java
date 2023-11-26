package cn.tuyucheng.taketoday.restvalidation.service3;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {FieldNotEmptyValidator.class})
public @interface FieldNotEmpty {

   String message() default "{validation.notEmpty}";

   String field() default "Field";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}