package cn.tuyucheng.taketoday.environmentpostprocessor.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class GrossPriceCalculator implements PriceCalculator {

	private static final Logger logger = LoggerFactory.getLogger(GrossPriceCalculator.class);

	@Value("${cn.tuyucheng.taketoday.environmentpostprocessor.gross.calculation.tax.rate}")
	double taxRate;

	@Override
	public double calculate(double singlePrice, int quantity) {
		logger.info("Gross based price calculation with input parameters [singlePrice = {},quantity= {} ], {} percent tax applied.", singlePrice, quantity, taxRate * 100);
		double netPrice = singlePrice * quantity;
		double result = Math.round(netPrice * (1 + taxRate));
		logger.info("Calcuation result is {}", result);
		return result;
	}
}