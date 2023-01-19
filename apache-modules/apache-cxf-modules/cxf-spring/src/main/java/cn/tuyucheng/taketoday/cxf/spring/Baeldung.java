package cn.tuyucheng.taketoday.cxf.spring;

import javax.jws.WebService;

@WebService
public interface Baeldung {
    String hello(String name);

    String register(Student student);
}