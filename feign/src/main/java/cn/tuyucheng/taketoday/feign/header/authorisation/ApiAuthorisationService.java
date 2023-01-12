package cn.tuyucheng.taketoday.feign.header.authorisation;

import java.util.UUID;

public class ApiAuthorisationService implements AuthorisationService {

	@Override
	public String getAuthToken() {
		return "Bearer " + UUID.randomUUID();
	}
}
