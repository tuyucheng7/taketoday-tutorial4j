package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.common.AbstractService;
import cn.tuyucheng.taketoday.persistence.common.IOperations;
import cn.tuyucheng.taketoday.persistence.dao.IParentDao;
import cn.tuyucheng.taketoday.persistence.model.Parent;
import cn.tuyucheng.taketoday.persistence.service.IParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService extends AbstractService<Parent> implements IParentService {

	@Autowired
	private IParentDao dao;

	public ParentService() {
		super();
	}

	// API

	@Override
	protected IOperations<Parent> getDao() {
		return dao;
	}

}
