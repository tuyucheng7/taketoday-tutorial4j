package cn.tuyucheng.taketoday.swaggerresponses.service;

import cn.tuyucheng.taketoday.swaggerresponses.response.CustomerResponse;

public interface CustomerService {

	CustomerResponse getById(Long id);
}