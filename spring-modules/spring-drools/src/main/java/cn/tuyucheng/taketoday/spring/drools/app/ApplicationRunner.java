package cn.tuyucheng.taketoday.spring.drools.app;

import cn.tuyucheng.taketoday.spring.drools.model.Fare;
import cn.tuyucheng.taketoday.spring.drools.model.TaxiRide;
import cn.tuyucheng.taketoday.spring.drools.service.TaxiFareCalculatorService;
import cn.tuyucheng.taketoday.spring.drools.service.TaxiFareConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationRunner {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(TaxiFareConfiguration.class);
		TaxiFareCalculatorService taxiFareCalculatorService = (TaxiFareCalculatorService) context.getBean(TaxiFareCalculatorService.class);
		TaxiRide taxiRide = new TaxiRide();
		taxiRide.setIsNightSurcharge(true);
		taxiRide.setDistanceInMile(190L);
		Fare rideFare = new Fare();
		taxiFareCalculatorService.calculateFare(taxiRide, rideFare);
	}
}