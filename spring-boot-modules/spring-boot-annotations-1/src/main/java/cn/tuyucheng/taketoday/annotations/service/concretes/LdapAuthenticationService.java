package cn.tuyucheng.taketoday.annotations.service.concretes;

import cn.tuyucheng.taketoday.annotations.service.abstracts.AbstractAuthenticationService;
import org.springframework.stereotype.Service;

@Service
public class LdapAuthenticationService extends AbstractAuthenticationService {

	@Override
	public boolean authenticate(String username, String password) {
		return true;
	}
}