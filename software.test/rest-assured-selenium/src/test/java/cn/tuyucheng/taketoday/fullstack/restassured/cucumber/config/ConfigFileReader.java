package cn.tuyucheng.taketoday.fullstack.restassured.cucumber.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

	private final Properties properties;
	private static ConfigFileReader configFileReader;

	private ConfigFileReader() {
		BufferedReader reader;
		String propertyFilePath = "source/testing/resources/config/configuration.properties";
		try {
			reader = new BufferedReader(new FileReader(propertyFilePath));
			properties = new Properties();
			try {
				properties.load(reader);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("configuration.properties not found at " + propertyFilePath);
		}
	}

	public static ConfigFileReader getInstance() {
		if (configFileReader == null)
			configFileReader = new ConfigFileReader();
		return configFileReader;
	}

	public String getBaseUrl() {
		String baseUrl = properties.getProperty("base_Url");
		if (baseUrl != null)
			return baseUrl;
		else
			throw new RuntimeException("base_Url not specified in the configuration.properties file");
	}

	public String getUserID() {
		String userId = properties.getProperty("user_Id");
		if (userId != null)
			return userId;
		else
			throw new RuntimeException("user_Id not specified in the configuration.properties file.");
	}
}