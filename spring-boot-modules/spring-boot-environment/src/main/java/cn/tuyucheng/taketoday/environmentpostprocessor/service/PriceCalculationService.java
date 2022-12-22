package cn.tuyucheng.taketoday.environmentpostprocessor.service;

import cn.tuyucheng.taketoday.environmentpostprocessor.calculator.PriceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationService {

	@Autowired
	PriceCalculator priceCalculator;

	public double productTotalPrice(double singlePrice, int quantity) {
		return priceCalculator.calculate(singlePrice, quantity);
	}
}