package cn.tuyucheng.taketoday.security.opa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpaProperties.class)
public class OpaConfiguration {

    private final OpaProperties opaProperties;

    @Bean
    public WebClient opaWebClient(WebClient.Builder builder) {

        return builder
              .baseUrl(opaProperties.getEndpoint())
              .build();
    }

}
