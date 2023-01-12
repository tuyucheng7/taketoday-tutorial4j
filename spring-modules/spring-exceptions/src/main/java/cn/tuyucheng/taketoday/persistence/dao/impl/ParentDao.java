package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.common.AbstractHibernateDao;
import cn.tuyucheng.taketoday.persistence.dao.IParentDao;
import cn.tuyucheng.taketoday.persistence.model.Parent;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParentDao extends AbstractHibernateDao<Parent> implements IParentDao {

	@Autowired
	private SessionFactory sessionFactory;

	public ParentDao() {
		super();

		setClazz(Parent.class);
	}

	// API

}
