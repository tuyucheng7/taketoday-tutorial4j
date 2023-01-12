package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFooDao extends JpaRepository<Foo, Long> {

}
