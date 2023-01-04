package cn.tuyucheng.taketoday.spring.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WelcomeController extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView model = new ModelAndView("welcome");
        model.addObject("msg", " Tuyucheng's Spring Handler Mappings Guide.<br>This request was mapped" + " using SimpleUrlHandlerMapping.");

        return model;
    }
}