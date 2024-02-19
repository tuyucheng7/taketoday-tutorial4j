package cn.tuyucheng.taketoday.failureanalyzer;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MyService {

   @Resource(name = "myDAO")
   private MyDAO myDAO;
}