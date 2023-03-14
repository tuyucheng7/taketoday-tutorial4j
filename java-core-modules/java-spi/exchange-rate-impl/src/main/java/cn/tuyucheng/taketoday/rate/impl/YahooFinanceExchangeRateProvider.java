package cn.tuyucheng.taketoday.rate.impl;

import cn.tuyucheng.taketoday.rate.api.QuoteManager;
import cn.tuyucheng.taketoday.rate.spi.ExchangeRateProvider;

public class YahooFinanceExchangeRateProvider implements ExchangeRateProvider {

	@Override
	public QuoteManager create() {
		return new YahooQuoteManagerImpl();
	}
}