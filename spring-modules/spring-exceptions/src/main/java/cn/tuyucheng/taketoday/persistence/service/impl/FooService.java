package cn.tuyucheng.taketoday.persistence.service.impl;

import cn.tuyucheng.taketoday.persistence.common.AbstractService;
import cn.tuyucheng.taketoday.persistence.common.IOperations;
import cn.tuyucheng.taketoday.persistence.dao.IFooDao;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.persistence.service.IFooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FooService extends AbstractService<Foo> implements IFooService {

	@Autowired
	private IFooDao dao;

	public FooService() {
		super();
	}

	// API

	@Override
	protected IOperations<Foo> getDao() {
		return dao;
	}

}
