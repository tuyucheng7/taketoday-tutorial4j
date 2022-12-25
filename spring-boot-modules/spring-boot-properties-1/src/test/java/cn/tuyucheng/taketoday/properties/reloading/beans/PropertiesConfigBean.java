package cn.tuyucheng.taketoday.properties.reloading.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PropertiesConfigBean {

	private Properties properties;

	public PropertiesConfigBean(@Autowired Properties properties) {
		this.properties = properties;
	}

	public String getColor() {
		return properties.getProperty("application.theme.color");
	}
}