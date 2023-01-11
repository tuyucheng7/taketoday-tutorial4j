package cn.tuyucheng.taketoday.session.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class SessionRestController {

    @GetMapping("/session-max-interval")
    public String retrieveMaxSessionInactiveInterval(HttpSession session) {
        return "Max Inactive Interval before Session expires: " + session.getMaxInactiveInterval();
    }
}
