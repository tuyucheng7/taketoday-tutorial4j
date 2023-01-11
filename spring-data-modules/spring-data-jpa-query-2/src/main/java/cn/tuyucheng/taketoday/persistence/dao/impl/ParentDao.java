package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.dao.IParentDao;
import cn.tuyucheng.taketoday.persistence.model.Parent;
import cn.tuyucheng.taketoday.persistence.dao.common.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class ParentDao extends AbstractHibernateDao<Parent> implements IParentDao {

    public ParentDao() {
        super();
        setClazz(Parent.class);
    }
}