package cn.tuyucheng.taketoday.ex.dataintegrityviolationexception;

import cn.tuyucheng.taketoday.ex.dataintegrityviolationexception.spring.Cause1DataContextWithJavaConfig;
import cn.tuyucheng.taketoday.persistence.model.Child;
import cn.tuyucheng.taketoday.persistence.model.Parent;
import cn.tuyucheng.taketoday.persistence.service.IChildService;
import cn.tuyucheng.taketoday.persistence.service.IParentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Cause1DataContextWithJavaConfig.class}, loader = AnnotationConfigContextLoader.class)
public class Cause1DataIntegrityViolationExceptionManualTest {

	@Autowired
	private IParentService service;

	@Autowired
	private IChildService childService;

	// tests

	@Test(expected = DataIntegrityViolationException.class)
	public void whenChildIsDeletedWhileParentStillHasForeignKeyToIt_thenDataException() {
		final Child childEntity = new Child();
		childService.create(childEntity);

		final Parent parentEntity = new Parent(childEntity);
		service.create(parentEntity);

		childService.delete(childEntity);
	}

	@Test
	public void whenChildIsDeletedAfterTheParent_thenNoExceptions() {
		final Child childEntity = new Child();
		childService.create(childEntity);

		final Parent parentEntity = new Parent(childEntity);
		service.create(parentEntity);

		service.delete(parentEntity);
		childService.delete(childEntity);
	}

}
