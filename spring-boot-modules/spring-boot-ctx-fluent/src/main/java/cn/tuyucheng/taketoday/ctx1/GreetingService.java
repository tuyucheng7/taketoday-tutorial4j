package cn.tuyucheng.taketoday.ctx1;

import cn.tuyucheng.taketoday.parent.HomeService;
import org.springframework.stereotype.Service;

@Service
public class GreetingService implements HomeService {

    public String getGreeting() {
        return "Greetings for the day";
    }
}