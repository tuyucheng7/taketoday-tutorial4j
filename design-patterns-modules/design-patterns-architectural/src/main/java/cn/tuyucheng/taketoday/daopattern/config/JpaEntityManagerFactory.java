package cn.tuyucheng.taketoday.daopattern.config;

import cn.tuyucheng.taketoday.daopattern.entities.User;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JpaEntityManagerFactory {

	private final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private final String DB_USER_NAME = "username";
	private final String DB_PASSWORD = "password";

	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	protected EntityManagerFactory getEntityManagerFactory() {
		PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
		Map<String, Object> configuration = new HashMap<>();
		return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
			.build();
	}

	protected PersistenceUnitInfoImpl getPersistenceUnitInfo(String name) {
		return new PersistenceUnitInfoImpl(name, getEntityClassNames(), getProperties());
	}

	protected List<String> getEntityClassNames() {
		return Arrays.stream(getEntities())
			.map(Class::getName)
			.toList();
	}

	protected Properties getProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		properties.put("hibernate.id.new_generator_mappings", false);
		properties.put("hibernate.connection.datasource", getMysqlDataSource());
		return properties;
	}

	protected Class[] getEntities() {
		return new Class[]{User.class};
	}

	protected DataSource getMysqlDataSource() {
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(DB_URL);
		mysqlDataSource.setUser(DB_USER_NAME);
		mysqlDataSource.setPassword(DB_PASSWORD);
		return mysqlDataSource;
	}
}