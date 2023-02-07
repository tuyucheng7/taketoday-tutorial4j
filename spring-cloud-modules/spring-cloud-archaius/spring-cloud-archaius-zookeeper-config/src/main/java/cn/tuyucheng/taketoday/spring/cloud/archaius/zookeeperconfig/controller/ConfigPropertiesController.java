package cn.tuyucheng.taketoday.spring.cloud.archaius.zookeeperconfig.controller;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfigPropertiesController {

	@Value("${tuyucheng.archaius.properties.one:not found!}")
	private String propertyOneWithValue;

	@Value("${tuyucheng.archaius.properties.two:not found!}")
	private String propertyTwoWithValue;

	@Value("${tuyucheng.archaius.properties.three:not found!}")
	private String propertyThreeWithValue;

	private DynamicStringProperty propertyOneWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.one", "not found!");

	private DynamicStringProperty propertyTwoWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.two", "not found!");

	private DynamicStringProperty propertyThreeWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.three", "not found!");

	@GetMapping("/properties-from-dynamic")
	public Map<String, String> getPropertiesFromDynamic() {
		Map<String, String> properties = new HashMap<>();
		properties.put(propertyOneWithDynamic.getName(), propertyOneWithDynamic.get());
		properties.put(propertyTwoWithDynamic.getName(), propertyTwoWithDynamic.get());
		properties.put(propertyThreeWithDynamic.getName(), propertyThreeWithDynamic.get());
		return properties;
	}

	@GetMapping("/properties-from-value")
	public Map<String, String> getPropertiesFromValue() {
		Map<String, String> properties = new HashMap<>();
		properties.put("tuyucheng.archaius.properties.one", propertyOneWithValue);
		properties.put("tuyucheng.archaius.properties.two", propertyTwoWithValue);
		properties.put("tuyucheng.archaius.properties.three", propertyThreeWithValue);
		return properties;
	}
}