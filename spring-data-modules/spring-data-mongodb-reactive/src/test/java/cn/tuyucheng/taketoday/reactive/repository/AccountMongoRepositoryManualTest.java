package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.reactive.Spring5ReactiveApplication;
import cn.tuyucheng.taketoday.reactive.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Spring5ReactiveApplication.class)
class AccountMongoRepositoryManualTest {

   @Autowired
   AccountMongoRepository repository;

   @Test
   void givenExample_whenFindAllWithExample_thenFindAllMacthings() {
      repository.save(new Account(null, "john", 12.3)).block();
      ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("owner", startsWith());
      Example<Account> example = Example.of(new Account(null, "jo", null), matcher);
      Flux<Account> accountFlux = repository.findAll(example);

      StepVerifier
            .create(accountFlux)
            .assertNext(account -> assertEquals("john", account.getOwner()))
            .expectComplete()
            .verify();
   }

   @Test
   void givenAccount_whenSave_thenSave() {
      Mono<Account> accountMono = repository.save(new Account(null, "john", 12.3));

      StepVerifier
            .create(accountMono)
            .assertNext(account -> assertNotNull(account.getId()))
            .expectComplete()
            .verify();
   }

   @Test
   void givenId_whenFindById_thenFindAccount() {
      Account inserted = repository.save(new Account(null, "john", 12.3)).block();
      Mono<Account> accountMono = repository.findById(inserted.getId());

      StepVerifier
            .create(accountMono)
            .assertNext(account -> {
               assertEquals("john", account.getOwner());
               assertEquals(Double.valueOf(12.3), account.getValue());
               assertNotNull(account.getId());
            })
            .expectComplete()
            .verify();
   }
}