package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.dao.IBarDao;
import cn.tuyucheng.taketoday.persistence.dao.common.AbstractHibernateDao;
import cn.tuyucheng.taketoday.persistence.model.Bar;
import org.springframework.stereotype.Repository;

@Repository
public class BarDao extends AbstractHibernateDao<Bar> implements IBarDao {

    public BarDao() {
        super();
        setClazz(Bar.class);
    }
}