package cn.tuyucheng.taketoday.boot.services.impl;

import com.google.common.collect.Lists;
import cn.tuyucheng.taketoday.boot.daos.FooDao;
import cn.tuyucheng.taketoday.boot.domain.Foo;
import cn.tuyucheng.taketoday.boot.services.FooService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FooServiceImpl extends AbstractService<Foo> implements FooService {

	@Autowired
	private FooDao dao;

	public FooServiceImpl() {
		super();
	}

	@Override
	protected PagingAndSortingRepository<Foo, Long> getDao() {
		return dao;
	}

	@Override
	public Foo retrieveByName(final String name) {
		return dao.retrieveByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Foo> findAll() {
		return Lists.newArrayList(getDao().findAll());
	}

	@Override
	public Page<Foo> findPaginated(Pageable pageable) {
		return dao.findAll(pageable);
	}
}