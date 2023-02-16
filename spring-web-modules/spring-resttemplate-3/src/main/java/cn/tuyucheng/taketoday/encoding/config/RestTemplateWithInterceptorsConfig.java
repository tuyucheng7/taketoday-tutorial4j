package cn.tuyucheng.taketoday.encoding.config;

import cn.tuyucheng.taketoday.encoding.UriEncodingInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateWithInterceptorsConfig {
    @Qualifier("restTemplateWithInterceptors")
    @Bean
    public RestTemplate restTemplateWithInterceptors() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new UriEncodingInterceptor()));
        return restTemplate;
    }
}
