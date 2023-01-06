package cn.tuyucheng.taketoday.boot.jdbi.dao;

import cn.tuyucheng.taketoday.boot.jdbi.domain.CarModel;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@UseClasspathSqlLocator
public interface CarModelDao {

	@SqlUpdate
	@GetGeneratedKeys
	Long insert(@BindBean CarModel carModel);

	@SqlBatch("insert")
	@GetGeneratedKeys
	List<Long> bulkInsert(@BindBean List<CarModel> carModels);

	@SqlQuery
	CarModel findByMakerIdAndSku(@Bind("makerId") Long makerId, @Bind("sku") String sku);
}