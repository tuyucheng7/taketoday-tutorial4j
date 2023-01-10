package cn.tuyucheng.taketoday.cloud.openfeign.client;

import cn.tuyucheng.taketoday.cloud.openfeign.model.Employee;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface EmployeeClient {
	@RequestLine("GET /empployee/{id}?active={isActive}")
	@Headers("Content-Type: application/json")
	Employee getEmployee(@Param long id, @Param boolean isActive);
}