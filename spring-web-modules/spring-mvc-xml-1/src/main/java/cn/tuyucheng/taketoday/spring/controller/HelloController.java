package cn.tuyucheng.taketoday.spring.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView("helloworld");
        model.addObject("msg", "Welcome to Tuyucheng's Spring Handler Mappings Guide.<br>This request was mapped" + " using SimpleUrlHandlerMapping.");

        return model;
    }
}