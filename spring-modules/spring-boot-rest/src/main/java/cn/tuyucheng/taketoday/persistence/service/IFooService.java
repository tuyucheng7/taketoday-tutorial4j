package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.IOperations;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IFooService extends IOperations<Foo> {

	Page<Foo> findPaginated(Pageable pageable);

}
