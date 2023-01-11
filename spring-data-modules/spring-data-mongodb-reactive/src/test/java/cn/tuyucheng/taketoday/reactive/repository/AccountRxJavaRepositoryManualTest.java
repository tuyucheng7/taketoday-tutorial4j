package cn.tuyucheng.taketoday.reactive.repository;

import cn.tuyucheng.taketoday.reactive.Spring5ReactiveApplication;
import cn.tuyucheng.taketoday.reactive.model.Account;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Spring5ReactiveApplication.class)
public class AccountRxJavaRepositoryManualTest {

	@Autowired
	AccountRxJavaRepository repository;

	@Test
	public void givenValue_whenFindAllByValue_thenFindAccounts() throws InterruptedException {
		repository.save(new Account(null, "bruno", 12.3)).blockingGet();
		Observable<Account> accountObservable = repository.findAllByValue(12.3);

		accountObservable
			.test()
			.await()
			.assertComplete()
			.assertValueAt(0, account -> {
				assertEquals("bruno", account.getOwner());
				assertEquals(Double.valueOf(12.3), account.getValue());
				return true;
			});

	}

	@Test
	public void givenOwner_whenFindFirstByOwner_thenFindAccount() throws InterruptedException {
		repository.save(new Account(null, "bruno", 12.3)).blockingGet();
		Single<Account> accountSingle = repository.findFirstByOwner(Single.just("bruno"));

		accountSingle
			.test()
			.await()
			.assertComplete()
			.assertValueAt(0, account -> {
				assertEquals("bruno", account.getOwner());
				assertEquals(Double.valueOf(12.3), account.getValue());
				assertNotNull(account.getId());
				return true;
			});

	}

}