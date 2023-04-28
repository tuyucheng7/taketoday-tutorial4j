package cn.tuyucheng.taketoday.spring.jinq.repositories;

import org.jinq.jpa.JPAJinqStream;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class BaseJinqRepositoryImpl<T> {
	@Autowired
	private JinqJPAStreamProvider jinqDataProvider;

	@PersistenceContext
	private EntityManager entityManager;

	protected abstract Class<T> entityType();

	public JPAJinqStream<T> stream() {
		return streamOf(entityType());
	}

	protected <U> JPAJinqStream<U> streamOf(Class<U> clazz) {
		return jinqDataProvider.streamAll(entityManager, clazz);
	}
}
