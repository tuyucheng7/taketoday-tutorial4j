package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.configuration.DataLoader;
import cn.tuyucheng.taketoday.dto.LoyaltyClientResponse;
import cn.tuyucheng.taketoday.utils.SleepUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyClient {

	private final DataLoader dataLoader;

	public Optional<LoyaltyClientResponse> getLoyaltyPointsByCustomerId(Integer customerId) {
		LOGGER.info("Getting loyalty points by customerId {}", customerId);
		SleepUtils.loadingSimulator(1);
		return Optional.ofNullable(new LoyaltyClientResponse(dataLoader.getPoints().get(customerId)));
	}
}