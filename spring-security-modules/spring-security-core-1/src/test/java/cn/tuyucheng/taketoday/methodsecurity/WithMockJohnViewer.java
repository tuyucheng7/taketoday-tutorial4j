package cn.tuyucheng.taketoday.methodsecurity;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "john", roles = "VIEWER")
public @interface WithMockJohnViewer {
}