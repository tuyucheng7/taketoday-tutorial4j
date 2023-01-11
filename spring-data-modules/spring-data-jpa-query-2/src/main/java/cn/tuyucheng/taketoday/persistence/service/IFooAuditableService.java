package cn.tuyucheng.taketoday.persistence.service;

import cn.tuyucheng.taketoday.persistence.dao.common.IAuditOperations;
import cn.tuyucheng.taketoday.persistence.model.Foo;

public interface IFooAuditableService extends IFooService, IAuditOperations<Foo> {
    //
}