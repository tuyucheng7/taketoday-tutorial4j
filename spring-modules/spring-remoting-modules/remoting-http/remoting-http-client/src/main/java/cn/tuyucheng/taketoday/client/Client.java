package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.api.BookingException;
import cn.tuyucheng.taketoday.api.CabBookingService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import static java.lang.System.out;

@Configuration
public class Client {

	@Bean
	public HttpInvokerProxyFactoryBean invoker() {
		HttpInvokerProxyFactoryBean invoker = new HttpInvokerProxyFactoryBean();
		invoker.setServiceUrl("http://localhost:8080/booking");
		invoker.setServiceInterface(CabBookingService.class);
		return invoker;
	}

	public static void main(String[] args) throws BookingException {
		CabBookingService service = SpringApplication.run(Client.class, args).getBean(CabBookingService.class);
		out.println(service.bookRide("13 Seagate Blvd, Key Largo, FL 33037"));
	}

}
