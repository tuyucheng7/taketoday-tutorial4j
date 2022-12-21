package cn.tuyucheng.taketoday.session.exception.repository;

import cn.tuyucheng.taketoday.demo.model.Foo;

public interface FooRepository {

    void save(Foo foo);

    Foo get(Integer id);
}