package cn.tuyucheng.taketoday.guice.service.impl;

import java.util.UUID;

import cn.tuyucheng.taketoday.guice.service.DataPumpService;

public class DataPumpServiceImpl implements DataPumpService {

	@Override
	public String generate() {
		return UUID.randomUUID().toString();
	}

}
