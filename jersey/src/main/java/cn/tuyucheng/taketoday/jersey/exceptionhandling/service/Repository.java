package cn.tuyucheng.taketoday.jersey.exceptionhandling.service;

import cn.tuyucheng.taketoday.jersey.exceptionhandling.data.Stock;
import cn.tuyucheng.taketoday.jersey.exceptionhandling.data.Wallet;
import cn.tuyucheng.taketoday.jersey.exceptionhandling.repo.Db;

public class Repository {
	public static Db<Stock> STOCKS_DB = new Db<>();
	public static Db<Wallet> WALLETS_DB = new Db<>();
}
