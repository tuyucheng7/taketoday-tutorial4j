package cn.tuyucheng.taketoday.webflux.onsuccess;

import cn.tuyucheng.taketoday.webflux.model.Payment;

public class PaymentService {

	public void processPayment(Payment payment) {
		System.err.println("Payment processed: " + payment);
	}
}