package cn.tuyucheng.taketoday.feign.header.interceptor;

import cn.tuyucheng.taketoday.feign.header.authorisation.AuthorisationService;
import feign.RequestInterceptor;
import feign.RequestTemplate;


public class AuthRequestInterceptor implements RequestInterceptor {

	private AuthorisationService authTokenService;

	public AuthRequestInterceptor(AuthorisationService authTokenService) {
		this.authTokenService = authTokenService;
	}

	@Override
	public void apply(RequestTemplate template) {
		template.header("Authorisation", authTokenService.getAuthToken());
	}
}

