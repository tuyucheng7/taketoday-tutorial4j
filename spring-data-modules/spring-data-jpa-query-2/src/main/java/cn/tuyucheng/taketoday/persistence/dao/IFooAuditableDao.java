package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.persistence.dao.common.IAuditOperations;

public interface IFooAuditableDao extends IFooDao, IAuditOperations<Foo> {
    //
}