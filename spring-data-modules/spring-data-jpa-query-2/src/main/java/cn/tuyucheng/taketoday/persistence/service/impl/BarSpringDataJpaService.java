package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.dao.IBarCrudRepository;
import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.service.IBarService;
import cn.tuyucheng.taketoday.persistence.service.common.AbstractSpringDataJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public class BarSpringDataJpaService extends AbstractSpringDataJpaService<Bar> implements IBarService {

    @Autowired
    private IBarCrudRepository dao;

    public BarSpringDataJpaService() {
        super();
    }

    @Override
    protected CrudRepository<Bar, Serializable> getDao() {
        return dao;
    }
}