package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.configuration.DataLoader;
import cn.tuyucheng.taketoday.dto.PurchaseTransaction;
import cn.tuyucheng.taketoday.utils.SleepUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseTransactionClient {

	private final DataLoader dataLoader;

	public Set<PurchaseTransaction> getPurchaseTransactionsByCustomerId(Integer customerId) {
		LOGGER.info("Getting purchase transactions by customerId {}", customerId);
		SleepUtils.loadingSimulator(3);
		return dataLoader.getPurchaseTransactionResponses().get(customerId);
	}
}