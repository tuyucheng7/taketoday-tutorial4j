package cn.tuyucheng.taketoday.schemageneration.repository;

import cn.tuyucheng.taketoday.schemageneration.model.AccountSetting;
import org.springframework.data.repository.CrudRepository;

public interface AccountSettingRepository extends CrudRepository<AccountSetting, Long> {
	AccountSetting findByAccountId(Long accountId);
}