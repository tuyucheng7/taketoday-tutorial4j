package cn.tuyucheng.taketoday.serializeentityid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

	@Autowired
	private EntityManager entityManager;

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		final Class<?>[] classes = this.entityManager.getMetamodel()
				.getEntities()
				.stream()
				.map(Type::getJavaType)
				.toArray(Class[]::new);
		config.exposeIdsFor(classes);
	}
}