package cn.tuyucheng.taketoday.mapstruct.mappingCollections.mapper;

import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.EmployeeFullNameDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface EmployeeFullNameMapper {

	List<EmployeeFullNameDTO> map(List<Employee> employees);

	default EmployeeFullNameDTO map(Employee employee) {
		EmployeeFullNameDTO employeeInfoDTO = new EmployeeFullNameDTO();
		employeeInfoDTO.setFullName(employee.getFirstName() + " " + employee.getLastName());

		return employeeInfoDTO;
	}
}
