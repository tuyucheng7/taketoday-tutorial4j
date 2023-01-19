package cn.tuyucheng.taketoday.jcommander.usagebilling.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
public class CurrentChargesRequest {

	private String customerId;
	private List<String> subscriptionIds;
	private boolean itemized;
}
