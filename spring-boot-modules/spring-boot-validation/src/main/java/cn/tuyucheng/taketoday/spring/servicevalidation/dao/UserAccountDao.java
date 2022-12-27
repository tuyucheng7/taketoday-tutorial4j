package cn.tuyucheng.taketoday.spring.servicevalidation.dao;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import cn.tuyucheng.taketoday.spring.servicevalidation.domain.UserAccount;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@ExcludeFromJacocoGeneratedReport
public class UserAccountDao {

	private Map<String, UserAccount> DB = new HashMap<>();

	public String addUserAccount(UserAccount useraccount) {
		DB.put(useraccount.getName(), useraccount);
		return "success";
	}

	public Collection<UserAccount> getAllUserAccounts() {
		Collection<UserAccount> list = DB.values();
		if (list.isEmpty()) {
			list.addAll(DB.values());
		}
		return list;
	}
}