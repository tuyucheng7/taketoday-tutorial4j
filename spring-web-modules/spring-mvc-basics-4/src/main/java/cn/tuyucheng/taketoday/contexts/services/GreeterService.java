package cn.tuyucheng.taketoday.contexts.services;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.tuyucheng.taketoday.contexts.Greeting;

@Service
public class GreeterService {

   @Resource
   private Greeting greeting;

   public String greet() {
      return greeting.getMessage();
   }
}