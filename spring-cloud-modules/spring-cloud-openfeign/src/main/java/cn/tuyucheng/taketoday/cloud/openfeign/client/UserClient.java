package cn.tuyucheng.taketoday.cloud.openfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import cn.tuyucheng.taketoday.cloud.openfeign.config.FeignConfig;

@FeignClient(name = "user-client", url = "https://jsonplaceholder.typicode.com", configuration = FeignConfig.class)
public interface UserClient {

   @GetMapping(value = "/users")
   String getUsers();
}