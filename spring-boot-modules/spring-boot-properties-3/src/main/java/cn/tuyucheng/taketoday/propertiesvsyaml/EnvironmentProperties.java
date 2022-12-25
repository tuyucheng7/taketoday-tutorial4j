package cn.tuyucheng.taketoday.propertiesvsyaml;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ExcludeFromJacocoGeneratedReport
public class EnvironmentProperties {

	@Autowired
	private Environment env;

	public String getSomeKey() {
		return env.getProperty("key.something");
	}
}