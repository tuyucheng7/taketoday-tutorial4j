package cn.tuyucheng.taketoday.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class GetUserWithHTTPServletRequestController {

    public GetUserWithHTTPServletRequestController() {
        super();
    }

    @RequestMapping(value = "/username4", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameSimple(final HttpServletRequest request) {
        final Principal principal = request.getUserPrincipal();
        return principal.getName();
    }

}
