package cn.tuyucheng.taketoday.chainofresponsibility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainOfResponsibilityIntegrationTest {

	private static AuthenticationProcessor getChainOfAuthProcessor() {
		AuthenticationProcessor oAuthProcessor = new OAuthAuthenticationProcessor(null);
		return new UsernamePasswordAuthenticationProcessor(oAuthProcessor);
	}

	@Test
	void givenOAuthProvider_whenCheckingAuthorized_thenSuccess() {
		AuthenticationProcessor authProcessorChain = getChainOfAuthProcessor();
		boolean isAuthorized = authProcessorChain.isAuthorized(new OAuthTokenProvider());

		assertTrue(isAuthorized);
	}

	@Test
	void givenUsernamePasswordProvider_whenCheckingAuthorized_thenSuccess() {
		AuthenticationProcessor authProcessorChain = getChainOfAuthProcessor();
		boolean isAuthorized = authProcessorChain.isAuthorized(new UsernamePasswordProvider());

		assertTrue(isAuthorized);
	}

	@Test
	void givenSamlAuthProvider_whenCheckingAuthorized_thenFailure() {
		AuthenticationProcessor authProcessorChain = getChainOfAuthProcessor();
		boolean isAuthorized = authProcessorChain.isAuthorized(new SamlAuthenticationProvider());

		assertFalse(isAuthorized);
	}
}