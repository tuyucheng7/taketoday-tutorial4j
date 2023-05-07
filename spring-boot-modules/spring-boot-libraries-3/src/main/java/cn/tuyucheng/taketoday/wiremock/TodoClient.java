package cn.tuyucheng.taketoday.wiremock;

import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface TodoClient {

   @GetExchange("/")
   List<Todo> findAll();
}