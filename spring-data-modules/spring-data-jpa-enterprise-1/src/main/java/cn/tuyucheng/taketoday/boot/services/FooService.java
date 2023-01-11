package cn.tuyucheng.taketoday.boot.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.tuyucheng.taketoday.boot.domain.Foo;

public interface FooService extends Operations<Foo> {

	Foo retrieveByName(String name);

	Page<Foo> findPaginated(Pageable pageable);
}