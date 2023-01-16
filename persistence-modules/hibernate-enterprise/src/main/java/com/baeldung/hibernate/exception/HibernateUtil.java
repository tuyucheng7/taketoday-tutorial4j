package com.baeldung.hibernate.exception;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	private static String PROPERTY_FILE_NAME;

	public static SessionFactory getSessionFactory() throws IOException {
		return getSessionFactory(null);
	}

	public static SessionFactory getSessionFactory(String propertyFileName)
		throws IOException {
		PROPERTY_FILE_NAME = propertyFileName;
		if (sessionFactory == null) {
			ServiceRegistry serviceRegistry = configureServiceRegistry();
			sessionFactory = makeSessionFactory(serviceRegistry);
		}
		return sessionFactory;
	}

	private static SessionFactory makeSessionFactory(
		ServiceRegistry serviceRegistry) {
		MetadataSources metadataSources = new MetadataSources(serviceRegistry);
		metadataSources.addAnnotatedClass(Product.class);
		metadataSources.addAnnotatedClass(ProductEntity.class);
		Metadata metadata = metadataSources.getMetadataBuilder()
			.build();
		return metadata.getSessionFactoryBuilder()
			.build();

	}

	private static ServiceRegistry configureServiceRegistry()
		throws IOException {
		Properties properties = getProperties();
		return new StandardServiceRegistryBuilder().applySettings(properties)
			.build();
	}

	private static Properties getProperties() throws IOException {
		Properties properties = new Properties();
		URL propertiesURL = Thread.currentThread()
			.getContextClassLoader()
			.getResource(StringUtils.defaultString(PROPERTY_FILE_NAME,
				"hibernate-exception.properties"));
		try (FileInputStream inputStream = new FileInputStream(
			propertiesURL.getFile())) {
			properties.load(inputStream);
		}
		return properties;
	}
}