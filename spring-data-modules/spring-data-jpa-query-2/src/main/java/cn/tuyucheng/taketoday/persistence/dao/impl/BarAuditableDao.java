package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.dao.IBarAuditableDao;
import cn.tuyucheng.taketoday.persistence.dao.common.AbstractHibernateAuditableDao;
import cn.tuyucheng.taketoday.persistence.model.Bar;

import java.util.List;

public class BarAuditableDao extends AbstractHibernateAuditableDao<Bar> implements IBarAuditableDao {

    public BarAuditableDao() {
        super();

        setClazz(Bar.class);
    }

    @Override
    public List<Bar> getRevisions() {
        final List<Bar> resultList = super.getRevisions();
        for (final Bar bar : resultList) {
            bar.getFooSet().size(); // force FooSet initialization
        }
        return resultList;
    }
}