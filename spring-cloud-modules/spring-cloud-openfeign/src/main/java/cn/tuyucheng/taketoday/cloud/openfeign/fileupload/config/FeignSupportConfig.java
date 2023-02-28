package cn.tuyucheng.taketoday.cloud.openfeign.fileupload.config;

import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class FeignSupportConfig {
	@Bean
	public Encoder multipartFormEncoder() {
		return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
			@Override
			public HttpMessageConverters getObject() {
				return new HttpMessageConverters(new RestTemplate().getMessageConverters());
			}
		}));
	}

	@Bean
	public ErrorDecoder errorDecoder() {
		return new RetreiveMessageErrorDecoder();
	}
}