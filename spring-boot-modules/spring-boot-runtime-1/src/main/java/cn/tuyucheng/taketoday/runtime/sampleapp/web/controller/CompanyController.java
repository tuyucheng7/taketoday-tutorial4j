package cn.tuyucheng.taketoday.runtime.sampleapp.web.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tuyucheng.taketoday.runtime.sampleapp.web.dto.Company;

@RestController
public class CompanyController {

	@RequestMapping(value = "/companyRest", produces = MediaType.APPLICATION_JSON_VALUE)
	public Company getCompanyRest() {
		return new Company(1, "Xpto");
	}
}