package cn.tuyucheng.taketoday.service;

import cn.tuyucheng.taketoday.client.AddressClient;
import cn.tuyucheng.taketoday.client.FinancialClient;
import cn.tuyucheng.taketoday.client.LoyaltyClient;
import cn.tuyucheng.taketoday.client.PurchaseTransactionClient;
import cn.tuyucheng.taketoday.dto.Address;
import cn.tuyucheng.taketoday.dto.AddressResponse;
import cn.tuyucheng.taketoday.dto.CustomerResponse;
import cn.tuyucheng.taketoday.dto.FinancialInfo;
import cn.tuyucheng.taketoday.dto.FinancialResponse;
import cn.tuyucheng.taketoday.dto.LoyaltyClientResponse;
import cn.tuyucheng.taketoday.dto.LoyaltyResponse;
import cn.tuyucheng.taketoday.dto.PurchaseTransactionResponse;
import cn.tuyucheng.taketoday.dto.UpdateCustomerRequest;
import cn.tuyucheng.taketoday.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public record CustomerService(CustomerRepository customerRepository,
							  AddressClient addressClient,
							  PurchaseTransactionClient purchaseTransactionClient,
							  FinancialClient financialClient,
							  LoyaltyClient loyaltyClient) {

	public List<CustomerResponse> getAllCustomers() {
		LOGGER.info("Getting all customers");
		return customerRepository.findAll().stream()
			.map(CustomerResponse::valueOf)
			.collect(Collectors.toList());
	}

	public void replaceCustomer(Integer customerId, UpdateCustomerRequest request) {
		LOGGER.info("Replacing customer", customerId);
		LocalDateTime startTime = LocalDateTime.now();
		customerRepository.findById(customerId)
			.ifPresent(customerEntity -> {
				customerEntity.setPhoneNumber(request.getPhoneNumber());
				customerRepository.save(customerEntity);
			});
		Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
			.map(FinancialInfo::valueOf)
			.collect(Collectors.toSet());
		financialClient.updateFinancialInfo(customerId, financialInfo);

		Address address = Address.valueOf(request.getAddress());
		addressClient.updateAddressByCustomerId(customerId, address);
		LOGGER.info("Customer updated successfully!");
		LOGGER.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
	}

	public void updateCustomer(Integer customerId, UpdateCustomerRequest request) {
		LOGGER.info("Updating customer", customerId);
		LocalDateTime startTime = LocalDateTime.now();
		if (request.getPhoneNumber() != null) {
			LOGGER.info("Received a phone number, updating customer");
			customerRepository.findById(customerId)
				.ifPresent(customerEntity -> {
					customerEntity.setPhoneNumber(request.getPhoneNumber());
					customerRepository.save(customerEntity);
				});
		}
		if (!CollectionUtils.isEmpty(request.getFinancialInfo())) {
			LOGGER.info("Received a financial info, updating it");
			Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
				.map(FinancialInfo::valueOf)
				.collect(Collectors.toSet());
			financialClient.updateFinancialInfo(customerId, financialInfo);
		}
		if (request.getAddress() != null) {
			LOGGER.info("Received a address, updating it");
			Address address = Address.valueOf(request.getAddress());
			addressClient.updateAddressByCustomerId(customerId, address);
		}
		LOGGER.info("Customer updated successfully!");
		LOGGER.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
	}

	public CustomerResponse getCustomerById(Integer customerId) {
		LOGGER.info("Getting customer by id {} ", customerId);
		LocalDateTime startTime = LocalDateTime.now();
		CustomerResponse customerResponse = customerRepository.findById(customerId)
			.map(CustomerResponse::valueOf)
			.map(this::fetchCustomerInfo)
			.orElse(null);
		LOGGER.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
		return customerResponse;
	}

	private CustomerResponse fetchCustomerInfo(CustomerResponse customerResponse) {
		Integer customerId = customerResponse.getId();
		AddressResponse addressResponse = addressClient.getAddressByCustomerId(customerId)
			.map(AddressResponse::valueOf)
			.orElse(null);

		List<PurchaseTransactionResponse> purchaseTransactionResponses = Stream.ofNullable(
				purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
			.flatMap(Collection::stream)
			.map(PurchaseTransactionResponse::valueOf)
			.collect(Collectors.toList());

		List<FinancialResponse> financialResponses = Stream.ofNullable(
				financialClient.getFinancialInfoByCustomerId(customerId))
			.flatMap(Collection::stream)
			.map(FinancialResponse::valueOf)
			.collect(Collectors.toList());

		LoyaltyResponse loyaltyResponse = loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
			.map(LoyaltyClientResponse::getPoints)
			.map(LoyaltyResponse::new)
			.orElse(null);

		customerResponse.setAddressResponse(addressResponse);
		customerResponse.setPurchaseTransactions(purchaseTransactionResponses);
		customerResponse.setFinancialResponses(financialResponses);
		customerResponse.setLoyaltyResponse(loyaltyResponse);
		return customerResponse;
	}
}