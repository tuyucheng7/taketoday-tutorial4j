package cn.tuyucheng.taketoday.jcommander.usagebilling.service;

import cn.tuyucheng.taketoday.jcommander.usagebilling.model.CurrentChargesRequest;
import cn.tuyucheng.taketoday.jcommander.usagebilling.model.CurrentChargesResponse;

public interface FetchCurrentChargesService {

	static FetchCurrentChargesService getDefault() {
		return new DefaultFetchCurrentChargesService();
	}

	CurrentChargesResponse fetch(CurrentChargesRequest request);
}
