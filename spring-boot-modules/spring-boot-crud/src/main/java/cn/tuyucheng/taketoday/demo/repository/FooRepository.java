package cn.tuyucheng.taketoday.demo.repository;

import cn.tuyucheng.taketoday.demo.model.Foo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FooRepository extends JpaRepository<Foo, Integer> {
    Foo findByName(String name);
}