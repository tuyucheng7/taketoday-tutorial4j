package cn.tuyucheng.taketoday.ex.nontransientdataaccessexception;

import cn.tuyucheng.taketoday.ex.nontransientexception.cause.Cause1NonTransientConfig;
import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.persistence.service.IFooService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.sql.DataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Cause1NonTransientConfig.class}, loader = AnnotationConfigContextLoader.class)
public class DataIntegrityExceptionManualTest {

	@Autowired
	private IFooService fooService;

	@Autowired
	private DataSource restDataSource;

	@Test(expected = DataIntegrityViolationException.class)
	public void whenSavingNullValue_thenDataIntegrityException() {
		final Foo fooEntity = new Foo();
		fooService.create(fooEntity);
	}

	@Test(expected = DuplicateKeyException.class)
	public void whenSavingDuplicateKeyValues_thenDuplicateKeyException() {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(restDataSource);

		try {
			jdbcTemplate.execute("insert into foo(id,name) values (1,'a')");
			jdbcTemplate.execute("insert into foo(id,name) values (1,'b')");
		} finally {
			jdbcTemplate.execute("delete from foo where id=1");
		}
	}

}
