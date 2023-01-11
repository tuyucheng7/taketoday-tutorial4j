package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.service.common.AbstractJpaService;
import cn.tuyucheng.taketoday.persistence.dao.IBarDao;
import cn.tuyucheng.taketoday.persistence.dao.common.IOperations;
import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.service.IBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BarJpaService extends AbstractJpaService<Bar> implements IBarService {

    @Autowired
    @Qualifier("barJpaDao")
    private IBarDao dao;

    public BarJpaService() {
        super();
    }

    @Override
    protected IOperations<Bar> getDao() {
        return dao;
    }
}