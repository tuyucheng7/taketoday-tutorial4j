package cn.tuyucheng.taketoday.resttemplate.web.service;

import cn.tuyucheng.taketoday.resttemplate.web.dto.Person;

public interface PersonService {

    public Person saveUpdatePerson(Person person);

    public Person findPersonById(Integer id);
}