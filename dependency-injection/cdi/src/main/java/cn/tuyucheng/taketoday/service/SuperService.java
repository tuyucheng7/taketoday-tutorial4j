package cn.tuyucheng.taketoday.service;

import cn.tuyucheng.taketoday.interceptor.Audited;

public class SuperService {
	@Audited
	public String deliverService(String uid) {
		return uid;
	}
}
