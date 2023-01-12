package cn.tuyucheng.taketoday.mapstruct.mappingCollections.mapper;

import cn.tuyucheng.taketoday.mapstruct.mappingCollections.dto.EmployeeDTO;
import cn.tuyucheng.taketoday.mapstruct.mappingCollections.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface EmployeeMapper {

	EmployeeDTO map(Employee employee);

	List<EmployeeDTO> map(List<Employee> employees);

	Set<EmployeeDTO> map(Set<Employee> employees);

	Map<String, EmployeeDTO> map(Map<String, Employee> idEmployeeMap);
}
