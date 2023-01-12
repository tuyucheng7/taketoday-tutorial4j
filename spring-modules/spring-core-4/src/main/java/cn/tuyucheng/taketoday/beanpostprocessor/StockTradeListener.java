package cn.tuyucheng.taketoday.beanpostprocessor;

@FunctionalInterface
public interface StockTradeListener {

	void stockTradePublished(StockTrade trade);
}
