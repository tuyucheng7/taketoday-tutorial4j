package cn.tuyucheng.taketoday.webflux;

import lombok.Data;

@Data
public class EmployeeCreationEvent {
	private String employeeId;
	private String creationTime;

	public EmployeeCreationEvent(String employeeId, String creationTime) {
		super();
		this.employeeId = employeeId;
		this.creationTime = creationTime;
	}
}