package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.configuration.DataLoader;
import cn.tuyucheng.taketoday.dto.FinancialInfo;
import cn.tuyucheng.taketoday.utils.SleepUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialClient {

	private final DataLoader dataLoader;

	public Set<FinancialInfo> getFinancialInfoByCustomerId(Integer customerId) {
		LOGGER.info("Getting financial info by customerId {}", customerId);
		SleepUtils.loadingSimulator(2);
		return dataLoader.getFinancialResponses().get(customerId);

	}

	public void updateFinancialInfo(Integer customerId, Set<FinancialInfo> response) {
		LOGGER.info("Updating financial info by customerId {}", customerId);
		SleepUtils.loadingSimulator(2);
		dataLoader.getFinancialResponses().put(customerId, response);
	}
}