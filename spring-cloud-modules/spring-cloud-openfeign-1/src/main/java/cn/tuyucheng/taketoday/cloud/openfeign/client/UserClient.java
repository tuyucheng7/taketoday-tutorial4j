package cn.tuyucheng.taketoday.cloud.openfeign.client;

import cn.tuyucheng.taketoday.cloud.openfeign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "user-client", url = "https://jsonplaceholder.typicode.com", configuration = FeignConfig.class)
public interface UserClient {

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	String getUsers();
}