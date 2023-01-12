package cn.tuyucheng.taketoday.examples.security.sql;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
public class SqlInjectionSamplesApplicationUnitTest {

	@Autowired
	private AccountDAO target;

	@Test
	public void givenAVulnerableMethod_whenValidCustomerId_thenReturnSingleAccount() {
		List<AccountDTO> accounts = target.unsafeFindAccountsByCustomerId("C1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isNotEmpty();
		Assertions.assertThat(accounts).hasSize(1);
	}

	@Test
	public void givenAVulnerableMethod_whenHackedCustomerId_thenReturnAllAccounts() {
		List<AccountDTO> accounts = target.unsafeFindAccountsByCustomerId("C1' or '1'='1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isNotEmpty();
		Assertions.assertThat(accounts).hasSize(3);
	}

	@Test
	public void givenAVulnerableJpaMethod_whenHackedCustomerId_thenReturnAllAccounts() {
		List<AccountDTO> accounts = target.unsafeJpaFindAccountsByCustomerId("C1' or '1'='1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isNotEmpty();
		Assertions.assertThat(accounts).hasSize(3);
	}

	@Test
	public void givenASafeMethod_whenHackedCustomerId_thenReturnNoAccounts() {
		List<AccountDTO> accounts = target.safeFindAccountsByCustomerId("C1' or '1'='1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isEmpty();
	}

	@Test
	public void givenASafeJpaMethod_whenHackedCustomerId_thenReturnNoAccounts() {
		List<AccountDTO> accounts = target.safeJpaFindAccountsByCustomerId("C1' or '1'='1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isEmpty();
	}


	@Test
	public void givenASafeJpaCriteriaMethod_whenHackedCustomerId_thenReturnNoAccounts() {
		List<AccountDTO> accounts = target.safeJpaCriteriaFindAccountsByCustomerId("C1' or '1'='1");
		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts).isEmpty();
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenASafeMethod_whenInvalidOrderBy_thenThroweException() {
		target.safeFindAccountsByCustomerId("C1", "INVALID");
	}

	@Test(expected = Exception.class)
	public void givenWrongPlaceholderUsageMethod_whenNormalCall_thenThrowsException() {
		target.wrongCountRecordsByTableName("Accounts");
	}

	@Test(expected = Exception.class)
	public void givenWrongJpaPlaceholderUsageMethod_whenNormalCall_thenThrowsException() {
		target.wrongJpaCountRecordsByTableName("Accounts");
	}
}