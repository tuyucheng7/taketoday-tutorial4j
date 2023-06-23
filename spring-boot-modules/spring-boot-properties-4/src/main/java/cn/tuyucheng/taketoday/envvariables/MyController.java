package cn.tuyucheng.taketoday.envvariables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

	@Value("${environment.name}")
	private String environmentName;

	@Value("${java.home.and.environment}")
	private String javaHomeAndEnvironmentName;

	@Value("${thispropertydoesnotexist}")
	private String nonExistentProperty;

	@Value("${tuyucheng.presentation}")
	private String tuyuchengPresentation;

	@Autowired
	private Environment environment;

	@Autowired
	private TuyuchengProperties tuyuchengProperties;

	@GetMapping("/environment_name")
	String getEnvironmentName_FromEnvironmentVariables() {
		return environmentName;
	}

	@GetMapping("/java_home_and_environment")
	String getJavaHomeAndEnvironmentName_FromEnvironmentVariables() {
		return javaHomeAndEnvironmentName;
	}

	@GetMapping("non_existent_property")
	String getNonexistentProperty_FromEnvironmentVariables() {
		return nonExistentProperty;
	}

	@GetMapping("tuyucheng_presentation_from_value")
	String getTuyuchengPresentation_FromValue() {
		return tuyuchengPresentation;
	}

	@GetMapping("tuyucheng_presentation_from_environment")
	String getTuyuchengPresentation_FromEnvironment() {
		return environment.getProperty("tuyucheng.presentation");
	}

	@GetMapping("tuyucheng_configuration_properties")
	String getTuyuchengPresentation_FromConfigurationProperties() {
		return tuyuchengProperties.getPresentation();
	}
}