package cn.tuyucheng.taketoday.boot.jdbi.mapper;

import cn.tuyucheng.taketoday.boot.jdbi.domain.CarModel;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CarModelMapper implements RowMapper<CarModel> {

	@Override
	public CarModel map(ResultSet rs, StatementContext ctx) throws SQLException {
		return CarModel.builder()
			.id(rs.getLong("id"))
			.name(rs.getString("name"))
			.sku(rs.getString("sku"))
			.year(rs.getInt("year"))
			.build();
	}
}