package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.IOperations;
import cn.tuyucheng.taketoday.persistence.model.Foo;

public interface IFooService extends IOperations<Foo> {

   Foo retrieveByName(String name);

}
