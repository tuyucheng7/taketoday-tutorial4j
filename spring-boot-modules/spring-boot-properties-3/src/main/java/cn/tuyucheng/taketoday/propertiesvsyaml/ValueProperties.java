package cn.tuyucheng.taketoday.propertiesvsyaml;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.beans.factory.annotation.Value;

@ExcludeFromJacocoGeneratedReport
public class ValueProperties {

	@Value("${key.something}")
	private String injectedProperty;

	public String getAppName() {
		return injectedProperty;
	}
}