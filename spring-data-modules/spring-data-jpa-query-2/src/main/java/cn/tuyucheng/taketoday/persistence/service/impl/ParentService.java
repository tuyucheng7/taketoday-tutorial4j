package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.model.Parent;
import cn.tuyucheng.taketoday.persistence.service.IParentService;
import cn.tuyucheng.taketoday.persistence.dao.IParentDao;
import cn.tuyucheng.taketoday.persistence.dao.common.IOperations;
import cn.tuyucheng.taketoday.persistence.service.common.AbstractHibernateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService extends AbstractHibernateService<Parent> implements IParentService {

    @Autowired
    private IParentDao dao;

    public ParentService() {
        super();
    }

    @Override
    protected IOperations<Parent> getDao() {
        return dao;
    }
}