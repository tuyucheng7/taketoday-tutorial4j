package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.model.Bar;
import cn.tuyucheng.taketoday.persistence.dao.IBarDao;
import cn.tuyucheng.taketoday.persistence.dao.common.AbstractJpaDao;
import org.springframework.stereotype.Repository;

@Repository
public class BarJpaDao extends AbstractJpaDao<Bar> implements IBarDao {

    public BarJpaDao() {
        super();
        setClazz(Bar.class);
    }
}