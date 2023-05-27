package cn.tuyucheng.taketoday.resource.service.impl;

import cn.tuyucheng.taketoday.resource.persistence.model.Foo;
import cn.tuyucheng.taketoday.resource.persistence.repository.IFooRepository;
import cn.tuyucheng.taketoday.resource.service.IFooService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FooServiceImpl implements IFooService {

   private IFooRepository fooRepository;

   public FooServiceImpl(IFooRepository fooRepository) {
      this.fooRepository = fooRepository;
   }

   @Override
   public Optional<Foo> findById(Long id) {
      return fooRepository.findById(id);
   }

   @Override
   public Foo save(Foo foo) {
      return fooRepository.save(foo);
   }

   @Override
   public Iterable<Foo> findAll() {
      return fooRepository.findAll();
   }
}
