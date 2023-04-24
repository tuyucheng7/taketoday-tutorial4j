package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.entity.Customer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TcApplication {

	public static void main(String[] args) {
		SpringApplication.run(TcApplication.class, args);
	}

	@Bean
	ApplicationRunner jdbcRunner(JdbcTemplate template) {
		return args -> template.query("select * from customer", (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name")))
			.forEach(System.out::println);
	}
}