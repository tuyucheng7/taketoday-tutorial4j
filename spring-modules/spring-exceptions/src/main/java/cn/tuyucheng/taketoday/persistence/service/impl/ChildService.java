package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.common.AbstractService;
import cn.tuyucheng.taketoday.persistence.common.IOperations;
import cn.tuyucheng.taketoday.persistence.dao.IChildDao;
import cn.tuyucheng.taketoday.persistence.model.Child;
import cn.tuyucheng.taketoday.persistence.service.IChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService extends AbstractService<Child> implements IChildService {

	@Autowired
	private IChildDao dao;

	public ChildService() {
		super();
	}

	// API

	@Override
	protected IOperations<Child> getDao() {
		return dao;
	}

}
