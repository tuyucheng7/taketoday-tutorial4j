package cn.tuyucheng.taketoday.rxjava.jdbc;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rx.Observable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AutomapInterfaceIntegrationTest {

	private ConnectionProvider connectionProvider = Connector.connectionProvider;
	private Database db = Database.from(connectionProvider);

	private Observable<Integer> truncate = null;
	private Observable<Integer> insert1, insert2 = null;

	@BeforeEach
	void setup() {
		Observable<Integer> create = db.update("CREATE TABLE IF NOT EXISTS EMPLOYEE(id int primary key, name varchar(255))")
			.count();
		truncate = db.update("TRUNCATE TABLE EMPLOYEE")
			.dependsOn(create)
			.count();
		insert1 = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(1, 'Alan')")
			.dependsOn(truncate)
			.count();
		insert2 = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(2, 'Sarah')")
			.dependsOn(insert1)
			.count();
	}

	@Test
	void whenSelectFromTableAndAutomap_thenCorrect() {
		List<Employee> employees = db.select("select id, name from EMPLOYEE")
			.dependsOn(insert2)
			.autoMap(Employee.class)
			.toList()
			.toBlocking()
			.single();

		assertThat(employees.get(0)
			.id()).isEqualTo(1);
		assertThat(employees.get(0)
			.name()).isEqualTo("Alan");
		assertThat(employees.get(1)
			.id()).isEqualTo(2);
		assertThat(employees.get(1)
			.name()).isEqualTo("Sarah");
	}

	@AfterEach
	void close() {
		db.update("DROP TABLE EMPLOYEE")
			.dependsOn(truncate);
		connectionProvider.close();
	}
}