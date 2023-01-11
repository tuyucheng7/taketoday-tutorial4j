package cn.tuyucheng.taketoday.boot.services.impl;

import cn.tuyucheng.taketoday.boot.daos.BarCrudRepository;
import cn.tuyucheng.taketoday.boot.domain.Bar;
import cn.tuyucheng.taketoday.boot.services.BarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public class BarSpringDataJpaService extends AbstractSpringDataJpaService<Bar> implements BarService {

    @Autowired
    private BarCrudRepository dao;

    public BarSpringDataJpaService() {
        super();
    }

    @Override
    protected CrudRepository<Bar, Serializable> getDao() {
        return dao;
    }

    @Override
    public Page<Bar> findPaginated(int page, int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}