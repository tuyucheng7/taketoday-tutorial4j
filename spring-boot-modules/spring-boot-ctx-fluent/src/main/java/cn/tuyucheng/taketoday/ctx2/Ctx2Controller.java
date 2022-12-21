package cn.tuyucheng.taketoday.ctx2;

import cn.tuyucheng.taketoday.parent.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ctx2Controller {

    @Autowired
    private HomeService homeService;

    @GetMapping("/greeting")
    public String getGreeting() {
        return homeService.getGreeting();
    }
}