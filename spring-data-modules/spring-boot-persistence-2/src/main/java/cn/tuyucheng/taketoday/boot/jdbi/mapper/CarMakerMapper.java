package cn.tuyucheng.taketoday.boot.jdbi.mapper;

import cn.tuyucheng.taketoday.boot.jdbi.domain.CarMaker;
import cn.tuyucheng.taketoday.boot.jdbi.domain.CarModel;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class CarMakerMapper implements RowMapper<CarMaker> {

	@Override
	public CarMaker map(ResultSet rs, StatementContext ctx) throws SQLException {
		return CarMaker.builder()
			.id(rs.getLong("id"))
			.name(rs.getString("name"))
			.models(new ArrayList<CarModel>())
			.build();
	}
}