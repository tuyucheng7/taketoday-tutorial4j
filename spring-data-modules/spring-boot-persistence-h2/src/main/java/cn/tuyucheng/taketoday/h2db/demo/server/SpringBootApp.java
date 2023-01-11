package cn.tuyucheng.taketoday.h2db.demo.server;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Arrays;

@SpringBootApplication
@ComponentScan("cn.tuyucheng.taketoday.h2db.demo.server")
public class SpringBootApp {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);
	}

	@PostConstruct
	private void initDb() {
		System.out.printf("****** Creating table: %s, and Inserting test data ******%n", "Employees");

		String[] sqlStatements = {
			"drop table employees if exists",
			"create table employees(id serial,first_name varchar(255),last_name varchar(255))",
			"insert into employees(first_name, last_name) values('Eugen','Paraschiv')",
			"insert into employees(first_name, last_name) values('Scott','Tiger')"
		};

		Arrays.stream(sqlStatements).forEach(sql -> {
			System.out.println(sql);
			jdbcTemplate.execute(sql);
		});

		System.out.printf("****** Fetching from table: %s ******%n", "Employees");
		jdbcTemplate.query("select id,first_name,last_name from employees",
			(rs, i) -> {
				System.out.printf("id:%s,first_name:%s,last_name:%s%n",
					rs.getString("id"),
					rs.getString("first_name"),
					rs.getString("last_name"));
				return null;
			});
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseServer() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
	}
}