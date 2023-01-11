package cn.tuyucheng.taketoday.elementcollection;

import cn.tuyucheng.taketoday.elementcollection.model.Employee;
import cn.tuyucheng.taketoday.elementcollection.model.Phone;
import cn.tuyucheng.taketoday.elementcollection.repository.EmployeeRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ElementCollectionApplication.class)
class ElementCollectionIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void init() {
        Employee employee = new Employee(1, "Fred");
        employee.setPhones(
                Arrays.asList(new Phone("work", "+55", "99999-9999"), new Phone("home", "+55", "98888-8888")));
        employeeRepository.save(employee);
    }

    @AfterEach
    void clean() {
        employeeRepository.remove(1);
    }

    @Test
    void whenAccessLazyCollection_thenThrowLazyInitializationException() {
        Employee employee = employeeRepository.findById(1);
        assertThrows(LazyInitializationException.class, () -> assertThat(employee.getPhones().size(), is(2)));
    }

    @Test
    void whenUseJPAQL_thenFetchResult() {
        Employee employee = employeeRepository.findByJPQL(1);
        assertThat(employee.getPhones().size(), is(2));
    }

    @Test
    void whenUseEntityGraph_thenFetchResult() {
        Employee employee = employeeRepository.findByEntityGraph(1);
        assertThat(employee.getPhones().size(), is(2));
    }

    @Test
    @Transactional
    void whenUseTransaction_thenFetchResult() {
        Employee employee = employeeRepository.findById(1);
        assertThat(employee.getPhones().size(), is(2));
    }
}