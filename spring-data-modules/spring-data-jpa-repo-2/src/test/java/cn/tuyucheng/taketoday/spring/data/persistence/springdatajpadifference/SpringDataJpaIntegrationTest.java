package cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference;

import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.model.Employee;
import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.model.QEmployee;
import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.springdata.config.SpringDataJpaConfig;
import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.springdata.repository.EmployeeRepository;
import cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.springdata.repository.EmployeeRepositoryPagingAndSort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cn.tuyucheng.taketoday.spring.data.persistence.springdatajpadifference.TestUtils.employee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(classes = SpringDataJpaConfig.class)
@ExtendWith(SpringExtension.class)
@Transactional
@Rollback
class SpringDataJpaIntegrationTest {

   @Autowired
   private EmployeeRepository employeeRepository;

   @Autowired
   private EmployeeRepositoryPagingAndSort employeeRepositoryPagingAndSort;

   @Autowired
   private JPAQueryFactory jpaQueryFactory;

   @Test
   void givenPersistedEmployee_whenFindById_thenEmployeeIsFound() {
      Employee employee = employee("John", "Doe");

      employeeRepository.save(employee);

      assertEquals(Optional.of(employee), employeeRepository.findById(employee.getId()));
   }

   @Test
   void givenPersistedEmployee_whenFindByFirstName_thenEmployeeIsFound() {
      Employee employee = employee("John", "Doe");

      employeeRepository.save(employee);

      assertEquals(employee, employeeRepository.findByFirstName(employee.getFirstName())
            .get(0));
   }

   @Test
   void givenPersistedEmployee_whenUpdateEmployeeEmail_thenEmployeeHasUpdatedEmail() {
      Employee employee = employee("John", "Doe");

      employeeRepository.save(employee);

      Employee employeeToUpdate = employeeRepository.findById(employee.getId())
            .orElse(null);

      assertNotNull(employeeToUpdate);
      assertEquals(employee, employeeToUpdate);

      String updatedEmail = "email@gmail.com";

      employeeToUpdate.setEmail(updatedEmail);

      employeeRepository.save(employeeToUpdate);

      assertEquals(Optional.of(employeeToUpdate), employeeRepository.findById(employee.getId()));
   }

   @Test
   void givenPersistedEmployee_whenRemoveEmployee_thenNoEmployeeIsFound() {
      Employee employee = employee("John", "Doe");

      employeeRepository.save(employee);

      Employee persistedEmployee = employeeRepository.findById(employee.getId())
            .orElse(null);

      assertNotNull(persistedEmployee);

      employeeRepository.delete(persistedEmployee);

      assertFalse(employeeRepository.findById(employee.getId())
            .isPresent());
   }

   @Test
   void givenPersistedEmployees_whenFindSortedByFirstName_thenEmployeeAreFoundInOrder() {
      Employee john = employee("John", "Doe");
      Employee bob = employee("Bob", "Smith");
      Employee frank = employee("Frank", "Brown");

      employeeRepository.saveAll(Arrays.asList(john, bob, frank));

      List<Employee> employees = employeeRepository.findAllEmployee(Sort.by("firstName"));

      assertEquals(3, employees.size());
      assertEquals(bob, employees.get(0));
      assertEquals(frank, employees.get(1));
      assertEquals(john, employees.get(2));
   }

   @Test
   void givenPersistedEmployee_whenFindByQueryDsl_thenEmployeeIsFound() {
      Employee john = employee("John", "Doe");
      Employee frank = employee("Frank", "Doe");

      employeeRepository.saveAll(Arrays.asList(john, frank));

      QEmployee employeePath = QEmployee.employee;

      List<Employee> employees = jpaQueryFactory.selectFrom(employeePath)
            .where(employeePath.firstName.eq("John"), employeePath.lastName.eq("Doe"))
            .fetch();

      assertEquals(1, employees.size());
      assertEquals(john, employees.get(0));
   }

   @Test
   void givenPersistedEmployee_whenFindBySortAndPagingRepository_thenEmployeeAreFound() {
      Employee john = employee("John", "Doe");
      Employee bob = employee("Bob", "Smith");
      Employee frank = employee("Frank", "Brown");
      Employee jimmy = employee("Jimmy", "Armstrong");

      employeeRepositoryPagingAndSort.saveAll(Arrays.asList(john, bob, frank, jimmy));

      Pageable pageable = PageRequest.of(0, 2, Sort.by("firstName"));

      Page<Employee> employees = employeeRepositoryPagingAndSort.findAll(pageable);

      assertEquals(Arrays.asList(bob, frank), employees.get()
            .toList());
   }
}