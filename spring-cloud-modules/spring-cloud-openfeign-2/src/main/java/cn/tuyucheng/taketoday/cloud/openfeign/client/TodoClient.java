package cn.tuyucheng.taketoday.cloud.openfeign.client;

import cn.tuyucheng.taketoday.cloud.openfeign.model.Todo;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.net.URI;

@FeignClient(name = "todoClient")
public interface TodoClient {
	@RequestLine(value = "GET")
	Todo getTodoById(URI uri);
}