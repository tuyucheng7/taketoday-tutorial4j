package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.dao.IFooAuditableDao;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.persistence.dao.common.AbstractHibernateAuditableDao;

public class FooAuditableDao extends AbstractHibernateAuditableDao<Foo> implements IFooAuditableDao {

    public FooAuditableDao() {
        super();
        setClazz(Foo.class);
    }
}