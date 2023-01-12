package cn.tuyucheng.taketoday.persistence.dao.impl;

import cn.tuyucheng.taketoday.persistence.common.AbstractHibernateDao;
import cn.tuyucheng.taketoday.persistence.dao.IChildDao;
import cn.tuyucheng.taketoday.persistence.model.Child;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChildDao extends AbstractHibernateDao<Child> implements IChildDao {

	@Autowired
	private SessionFactory sessionFactory;

	public ChildDao() {
		super();

		setClazz(Child.class);
	}

	// API

}
