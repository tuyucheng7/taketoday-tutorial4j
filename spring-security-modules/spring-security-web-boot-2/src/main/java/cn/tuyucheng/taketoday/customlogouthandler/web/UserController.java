package cn.tuyucheng.taketoday.customlogouthandler.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.tuyucheng.taketoday.customlogouthandler.services.UserCache;
import cn.tuyucheng.taketoday.customlogouthandler.user.User;
import cn.tuyucheng.taketoday.customlogouthandler.user.UserUtils;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private final UserCache userCache;

    public UserController(UserCache userCache) {
        this.userCache = userCache;
    }

    @GetMapping(path = "/language")
    @ResponseBody
    public String getLanguage() {
        String userName = UserUtils.getAuthenticatedUserName();
        User user = userCache.getByUserName(userName);
        return user.getLanguage();
    }

}
