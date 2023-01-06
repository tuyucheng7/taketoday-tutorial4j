package cn.tuyucheng.taketoday.boot.jdbi.dao;

import cn.tuyucheng.taketoday.boot.jdbi.domain.CarMaker;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@UseClasspathSqlLocator
public interface CarMakerDao {

	@SqlUpdate
	@GetGeneratedKeys
	Long insert(@BindBean CarMaker carMaker);

	@SqlBatch("insert")
	@GetGeneratedKeys
	List<Long> bulkInsert(@BindBean List<CarMaker> carMakers);

	@SqlQuery
	CarMaker findById(@Bind("id") Long id);
}