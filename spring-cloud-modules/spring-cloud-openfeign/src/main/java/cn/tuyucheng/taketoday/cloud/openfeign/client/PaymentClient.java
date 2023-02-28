package cn.tuyucheng.taketoday.cloud.openfeign.client;

import cn.tuyucheng.taketoday.cloud.openfeign.model.Payment;
import cn.tuyucheng.taketoday.cloud.openfeign.oauthfeign.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "payment-client", url = "http://localhost:8081/resource-server-jwt", configuration = OAuthFeignConfig.class)
public interface PaymentClient {

	@RequestMapping(value = "/payments", method = RequestMethod.GET)
	List<Payment> getPayments();
}