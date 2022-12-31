package cn.tuyucheng.taketoday.kotlindsl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class Repository {

	private static final Map<Reporter.StockSymbol, List<Reporter.StockPrice>> priceBySymbol = Map.of(
		Reporter.StockSymbol.stockSymbol("TSL"), List.of(
			Reporter.StockPrice.stockPrice(new BigDecimal("1.32"), Instant.parse("2021-01-01T15:00:00Z")),
			Reporter.StockPrice.stockPrice(new BigDecimal("1.57"), Instant.parse("2021-05-05T15:00:00Z")),
			Reporter.StockPrice.stockPrice(new BigDecimal("1.89"), Instant.parse("2021-02-03T15:00:00Z"))
		),
		Reporter.StockSymbol.stockSymbol("FFB"), List.of(
			Reporter.StockPrice.stockPrice(new BigDecimal("5.32"), Instant.parse("2021-05-01T15:00:00Z")),
			Reporter.StockPrice.stockPrice(new BigDecimal("4.57"), Instant.parse("2021-02-05T15:00:00Z")),
			Reporter.StockPrice.stockPrice(new BigDecimal("9.89"), Instant.parse("2021-09-03T15:00:00Z"))
		)
	);

	public List<Reporter.StockPrice> getStockPrice(Reporter.StockSymbol stockSymbol) {
		if (!priceBySymbol.containsKey(stockSymbol)) {
			throw new RuntimeException("Invalid stock symbol " + stockSymbol);
		}
		return priceBySymbol.get(stockSymbol);
	}
}