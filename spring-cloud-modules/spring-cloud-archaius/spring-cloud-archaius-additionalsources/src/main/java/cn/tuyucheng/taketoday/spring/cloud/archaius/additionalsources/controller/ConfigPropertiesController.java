package cn.tuyucheng.taketoday.spring.cloud.archaius.additionalsources.controller;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfigPropertiesController {

	private DynamicStringProperty propertyOneWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.one", "not found!");

	private DynamicStringProperty propertyTwoWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.two", "not found!");

	private DynamicStringProperty propertyThreeWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.three", "not found!");

	private DynamicStringProperty propertyFourWithDynamic = DynamicPropertyFactory.getInstance()
		.getStringProperty("tuyucheng.archaius.properties.four", "not found!");

	@GetMapping("/properties-from-dynamic")
	public Map<String, String> getPropertiesFromDynamic() {
		Map<String, String> properties = new HashMap<>();
		properties.put(propertyOneWithDynamic.getName(), propertyOneWithDynamic.get());
		properties.put(propertyTwoWithDynamic.getName(), propertyTwoWithDynamic.get());
		properties.put(propertyThreeWithDynamic.getName(), propertyThreeWithDynamic.get());
		properties.put(propertyFourWithDynamic.getName(), propertyFourWithDynamic.get());
		return properties;
	}
}