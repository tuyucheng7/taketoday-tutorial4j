package cn.tuyucheng.taketoday.sampleapp.web.controller;

import cn.tuyucheng.taketoday.sampleapp.web.dto.Company;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

	@RequestMapping(value = "/companyRest", produces = MediaType.APPLICATION_JSON_VALUE)
	public Company getCompanyRest() {
		final Company company = new Company(1, "Xpto");
		return company;
	}
}