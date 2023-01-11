package cn.tuyucheng.taketoday.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class SecurityController {

    public SecurityController() {
        super();
    }

    // API

    @RequestMapping(value = "/username2", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(final Principal principal) {
        return principal.getName();
    }

}
