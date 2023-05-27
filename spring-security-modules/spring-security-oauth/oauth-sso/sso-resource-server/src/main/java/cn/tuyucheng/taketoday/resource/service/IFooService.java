package cn.tuyucheng.taketoday.resource.service;

import cn.tuyucheng.taketoday.resource.persistence.model.Foo;

import java.util.Optional;

public interface IFooService {
   Optional<Foo> findById(Long id);

   Foo save(Foo foo);

   Iterable<Foo> findAll();

}
