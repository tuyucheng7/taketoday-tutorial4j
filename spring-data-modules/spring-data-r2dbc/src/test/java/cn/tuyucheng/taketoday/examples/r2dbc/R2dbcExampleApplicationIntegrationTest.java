package cn.tuyucheng.taketoday.examples.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class R2dbcExampleApplicationIntegrationTest {

   @Autowired
   private WebTestClient webTestClient;

   @Autowired
   ConnectionFactory cf;

   @BeforeEach
   void initDatabase() {
      Flux.from(cf.create())
            .flatMap(c ->
                  c.createBatch()
                        .add("drop table if exists Account")
                        .add("create table Account(id IDENTITY(1,1), iban varchar(80) not null, balance DECIMAL(18,2) not null)")
                        .add("insert into Account(iban,balance) values ( 'BR430120980198201982', 100.00 ) ")
                        .add("insert into Account(iban,balance) values ( 'BR430120998729871000', 250.00 ) ")
                        .execute()
            )
            .log()
            .blockLast();
   }

   @Test
   void givenExistingAccountId_whenGetAccount_thenReturnExistingAccountInfo() {
      webTestClient
            .get()
            .uri("/accounts/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Account.class)
            .value((acc) -> assertThat(acc.getId(), is(1L)));
   }

   @Test
   void givenDatabaseHasSomeAccounts_whenGetAccount_thenReturnExistingAccounts() {
      webTestClient
            .get()
            .uri("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(List.class)
            .value((accounts) -> assertThat(accounts.size(), not(is(0))));
   }


   @Test
   void givenNewAccountData_whenPostAccount_thenReturnNewAccountInfo() {
      webTestClient
            .post()
            .uri("/accounts")
            .syncBody(new Account(null, "BR4303010298012098", 151.00))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Account.class)
            .value((acc) -> assertThat(acc.getId(), is(notNullValue())));
   }
}