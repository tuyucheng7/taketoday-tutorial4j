package cn.tuyucheng.taketoday.ctx1;

import cn.tuyucheng.taketoday.parent.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ctx1Controller {

    @Autowired
    private HomeService homeService;

    @GetMapping("/home")
    public String greeting() {
        return homeService.getGreeting();
    }
}