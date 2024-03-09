package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.springframework.stereotype.Repository;

@Repository
public class FooDao extends AbstractJpaDAO<Foo> implements IFooDao {

   public FooDao() {
      super();

      setClazz(Foo.class);
   }

   // API
}