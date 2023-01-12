package cn.tuyucheng.taketoday.petstore.app;

import cn.tuyucheng.taketoday.petstore.client.api.PetApi;
import cn.tuyucheng.taketoday.petstore.client.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PetStoreIntegrationConfig {

	@Bean
	public PetApi petpi() {
		return new PetApi(apiClient());
	}

	@Bean
	public ApiClient apiClient() {
		ApiClient apiClient = new ApiClient();
		return apiClient;
	}
}