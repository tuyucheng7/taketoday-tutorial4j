package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.dao.IChildDao;
import cn.tuyucheng.taketoday.persistence.service.IChildService;
import cn.tuyucheng.taketoday.persistence.service.common.AbstractHibernateService;
import cn.tuyucheng.taketoday.persistence.model.Child;
import cn.tuyucheng.taketoday.persistence.dao.common.IOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService extends AbstractHibernateService<Child> implements IChildService {

    @Autowired
    private IChildDao dao;

    public ChildService() {
        super();
    }

    @Override
    protected IOperations<Child> getDao() {
        return dao;
    }
}