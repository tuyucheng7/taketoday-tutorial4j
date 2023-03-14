package cn.tuyucheng.taketoday.rate.spi;

import cn.tuyucheng.taketoday.rate.api.QuoteManager;

public interface ExchangeRateProvider {
	QuoteManager create();
}