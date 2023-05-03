package cn.tuyucheng.taketoday;

import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface TodoClient {

   @GetExchange("/")
   List<Todo> findAll();
}