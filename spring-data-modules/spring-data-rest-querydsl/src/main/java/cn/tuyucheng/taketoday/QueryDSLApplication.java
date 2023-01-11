package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.controller.repository.AddressRepository;
import cn.tuyucheng.taketoday.controller.repository.UserRepository;
import cn.tuyucheng.taketoday.entity.Address;
import cn.tuyucheng.taketoday.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

/**
 * Note: In the IDE, remember to generate query type classes before running the Integration Test
 */
@SpringBootApplication
@EntityScan("cn.tuyucheng.taketoday.entity")
@EnableJpaRepositories("cn.tuyucheng.taketoday.controller.repository")
public class QueryDSLApplication {

    @Autowired
    private UserRepository personRepository;
    @Autowired
    private AddressRepository addressRepository;

    public static void main(String[] args) {
        SpringApplication.run(QueryDSLApplication.class, args);
    }

    @PostConstruct
    private void initializeData() {
        // Create John
        final User john = new User("John");
        personRepository.save(john);
        final Address addressOne = new Address("Fake Street 1", "Spain", john);
        addressRepository.save(addressOne);
        // Create Lisa
        final User lisa = new User("Lisa");
        personRepository.save(lisa);
        final Address addressTwo = new Address("Real Street 1", "Germany", lisa);
        addressRepository.save(addressTwo);
    }
}