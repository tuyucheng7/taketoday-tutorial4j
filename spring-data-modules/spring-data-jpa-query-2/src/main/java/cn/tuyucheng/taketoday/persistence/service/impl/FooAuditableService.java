package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.service.IFooAuditableService;
import cn.tuyucheng.taketoday.persistence.dao.common.IAuditOperations;
import cn.tuyucheng.taketoday.persistence.service.common.AbstractHibernateAuditableService;
import cn.tuyucheng.taketoday.persistence.dao.IFooAuditableDao;
import cn.tuyucheng.taketoday.persistence.dao.IFooDao;
import cn.tuyucheng.taketoday.persistence.dao.common.IOperations;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FooAuditableService extends AbstractHibernateAuditableService<Foo> implements IFooAuditableService {

    @Autowired
    @Qualifier("fooHibernateDao")
    private IFooDao dao;

    @Autowired
    @Qualifier("fooHibernateAuditableDao")
    private IFooAuditableDao auditDao;

    public FooAuditableService() {
        super();
    }

    @Override
    protected IOperations<Foo> getDao() {
        return dao;
    }

    @Override
    protected IAuditOperations<Foo> getAuditableDao() {
        return auditDao;
    }
}