package cn.tuyucheng.taketoday.spring.cloud.openfeign.client;

import cn.tuyucheng.taketoday.spring.cloud.openfeign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-client", url = "https://jsonplaceholder.typicode.com", configuration = FeignConfig.class)
public interface UserClient {

   @GetMapping(value = "/users")
   String getUsers();
}