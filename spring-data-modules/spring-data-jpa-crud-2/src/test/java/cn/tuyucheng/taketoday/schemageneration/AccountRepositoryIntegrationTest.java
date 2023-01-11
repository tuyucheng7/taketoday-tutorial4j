package cn.tuyucheng.taketoday.schemageneration;

import cn.tuyucheng.taketoday.schemageneration.model.Account;
import cn.tuyucheng.taketoday.schemageneration.model.AccountSetting;
import cn.tuyucheng.taketoday.schemageneration.repository.AccountRepository;
import cn.tuyucheng.taketoday.schemageneration.repository.AccountSettingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AccountApplication.class)
class AccountRepositoryIntegrationTest {

	private static final String USER_NAME = "Eduard";
	private static final String USER_EMAIL_ADDRESS = "eduard@gmx.com";
	private static final String ACCOUNT_SETTING_NAME = "Timezone";
	private static final String ACCOUNT_SETTING_VALUE = "UTC+02";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountSettingRepository accountSettingRepository;

	@AfterEach
	void tearDown() {
		accountRepository.deleteAll();
	}

	@Test
	void givenNewAccount_whenSave_thenSuccess() {
		Account account = new Account(USER_NAME, USER_EMAIL_ADDRESS);
		accountRepository.save(account);

		assertEquals(1, accountRepository.count());
	}

	@Test
	void givenSavedAccount_whenFindByName_thenFound() {
		Account account = new Account(USER_NAME, USER_EMAIL_ADDRESS);
		accountRepository.save(account);

		Account accountFound = accountRepository.findByName(USER_NAME);

		assertNotNull(accountFound);
		assertEquals(USER_NAME, accountFound.getName());
		assertEquals(USER_EMAIL_ADDRESS, accountFound.getEmailAddress());
	}

	@Test
	void givenSavedAccount_whenAccountSettingIsAdded_thenPersisted() {
		Account account = new Account(USER_NAME, USER_EMAIL_ADDRESS);
		account.addAccountSetting(new AccountSetting(ACCOUNT_SETTING_NAME, ACCOUNT_SETTING_VALUE));
		accountRepository.save(account);

		Account accountFound = accountRepository.findByName(USER_NAME);
		assertNotNull(accountFound);
		AccountSetting accountSetting = accountSettingRepository.findByAccountId(accountFound.getId());

		assertNotNull(accountSetting);
		assertEquals(ACCOUNT_SETTING_NAME, accountSetting.getSettingName());
		assertEquals(ACCOUNT_SETTING_VALUE, accountSetting.getSettingValue());
	}
}