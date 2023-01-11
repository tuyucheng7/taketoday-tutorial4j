package cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.springdata.repository;

import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.model.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepositoryPagingAndSort extends PagingAndSortingRepository<Employee, Long> {

}