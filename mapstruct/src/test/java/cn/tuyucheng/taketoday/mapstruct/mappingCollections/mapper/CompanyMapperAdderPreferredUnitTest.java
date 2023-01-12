package cn.tuyucheng.taketoday.mapstruct.mappingCollections.mapper;

import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.CompanyDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.EmployeeDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Company;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Employee;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperAdderPreferredUnitTest {

	private CompanyMapperAdderPreferred companyMapper = Mappers.getMapper(CompanyMapperAdderPreferred.class);

	@Test
	void whenMappingToCompanyDTO_thenExpectCorrectMappingResult() {
		Employee employee = new Employee("John", "Doe");
		Company company = new Company();
		company.setEmployees(Collections.singletonList(employee));

		CompanyDTO result = companyMapper.map(company);

		List<EmployeeDTO> employees = result.getEmployees();
		assertThat(employees).hasSize(1);
		assertThat(employees.get(0).getFirstName()).isEqualTo("John");
		assertThat(employees.get(0).getLastName()).isEqualTo("Doe");
	}
}