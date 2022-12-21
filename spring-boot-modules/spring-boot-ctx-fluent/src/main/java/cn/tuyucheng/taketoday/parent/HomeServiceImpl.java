package cn.tuyucheng.taketoday.parent;

import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    public String getGreeting() {
        return "Welcome User";
    }
}