package cn.tuyucheng.taketoday.integrationtesting;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SecuredService {

   @PreAuthorize("authenticated")
   public String sayHelloSecured() {
      return "Hello user.";
   }
}