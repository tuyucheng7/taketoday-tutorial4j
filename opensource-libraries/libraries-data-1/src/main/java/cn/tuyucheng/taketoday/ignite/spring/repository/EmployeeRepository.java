package cn.tuyucheng.taketoday.ignite.spring.repository;

import cn.tuyucheng.taketoday.ignite.spring.dto.EmployeeDTO;
import org.apache.ignite.springdata.repository.IgniteRepository;
import org.apache.ignite.springdata.repository.config.RepositoryConfig;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryConfig(cacheName = "baeldungCache")
public interface EmployeeRepository extends IgniteRepository<EmployeeDTO, Integer> {

	EmployeeDTO getEmployeeDTOById(Integer id);
}
