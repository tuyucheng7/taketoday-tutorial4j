package cn.tuyucheng.taketoday.jcommander.usagebilling.service;

import cn.tuyucheng.taketoday.jcommander.usagebilling.model.UsageRequest;

public interface SubmitUsageService {

	static SubmitUsageService getDefault() {
		return new DefaultSubmitUsageService();
	}

	String submit(UsageRequest request);
}
