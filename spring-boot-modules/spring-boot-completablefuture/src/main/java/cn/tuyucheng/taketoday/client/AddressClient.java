package cn.tuyucheng.taketoday.client;

import cn.tuyucheng.taketoday.configuration.DataLoader;
import cn.tuyucheng.taketoday.dto.Address;
import cn.tuyucheng.taketoday.utils.SleepUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressClient {

	private final DataLoader dataLoader;

	public Optional<Address> getAddressByCustomerId(Integer customerId) {
		LOGGER.info("Getting address by customerId {}", customerId);
		SleepUtils.loadingSimulator(1);
		return Optional.ofNullable(dataLoader.getAddressClientResponses().get(customerId));
	}

	public void updateAddressByCustomerId(Integer customerId, Address address) {
		LOGGER.info("Updating address by customerId {}", customerId);
		SleepUtils.loadingSimulator(1);
		dataLoader.getAddressClientResponses().put(customerId, address);
	}
}