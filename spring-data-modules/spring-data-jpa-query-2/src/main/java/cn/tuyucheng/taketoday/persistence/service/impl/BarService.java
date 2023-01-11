package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.service.IBarService;
import cn.tuyucheng.taketoday.persistence.service.common.AbstractHibernateService;
import cn.tuyucheng.taketoday.persistence.dao.IBarDao;
import cn.tuyucheng.taketoday.persistence.dao.common.IOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BarService extends AbstractHibernateService<Bar> implements IBarService {

    @Autowired
    @Qualifier("barHibernateDao")
    private IBarDao dao;

    public BarService() {
        super();
    }

    @Override
    protected IOperations<Bar> getDao() {
        return dao;
    }
}