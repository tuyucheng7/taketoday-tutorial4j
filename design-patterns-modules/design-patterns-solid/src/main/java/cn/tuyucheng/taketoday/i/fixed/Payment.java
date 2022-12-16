package cn.tuyucheng.taketoday.i.fixed;

import java.util.List;

public interface Payment {
	Object status();

	List<Object> getPayments();
}