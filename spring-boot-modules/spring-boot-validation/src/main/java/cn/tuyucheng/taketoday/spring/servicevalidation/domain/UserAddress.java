package cn.tuyucheng.taketoday.spring.servicevalidation.domain;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

import javax.validation.constraints.NotBlank;

@ExcludeFromJacocoGeneratedReport
public class UserAddress {

	@NotBlank
	private String countryCode;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}