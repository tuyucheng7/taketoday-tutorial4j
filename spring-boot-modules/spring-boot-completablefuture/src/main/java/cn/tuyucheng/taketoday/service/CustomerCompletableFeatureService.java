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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerCompletableFeatureService {

	private final CustomerRepository customerRepository;
	private final AddressClient addressClient;
	private final PurchaseTransactionClient purchaseTransactionClient;
	private final FinancialClient financialClient;
	private final LoyaltyClient loyaltyClient;

	public CompletableFuture<Void> replaceCustomer(Integer customerId, UpdateCustomerRequest request) {
		LOGGER.info("Replacing customer", customerId);
		CompletableFuture<Void> updateCustomerCF = CompletableFuture.runAsync(() -> {
			customerRepository.findById(customerId)
				.ifPresent(customerEntity -> {
					customerEntity.setPhoneNumber(request.getPhoneNumber());
					customerRepository.save(customerEntity);
				});
		});
		CompletableFuture<Void> updateFinancialInfoCF = CompletableFuture.runAsync(() -> {
			Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
				.map(FinancialInfo::valueOf)
				.collect(Collectors.toSet());
			financialClient.updateFinancialInfo(customerId, financialInfo);
		});
		CompletableFuture<Void> updateAddressCF = CompletableFuture.runAsync(() -> {
			Address address = Address.valueOf(request.getAddress());
			addressClient.updateAddressByCustomerId(customerId, address);
		});
		return CompletableFuture.allOf(updateCustomerCF, updateAddressCF,
			updateFinancialInfoCF);
	}

	public CompletableFuture<Void> updateCustomer(Integer customerId, UpdateCustomerRequest request) {
		LOGGER.info("Updating customer", customerId);
		CompletableFuture<Void> updateCustomerCF = null;
		if (request.getPhoneNumber() != null) {
			LOGGER.info("Received a phone number, updating customer");
			updateCustomerCF = CompletableFuture.runAsync(() -> {
				customerRepository.findById(customerId)
					.ifPresent(customerEntity -> {
						customerEntity.setPhoneNumber(request.getPhoneNumber());
						customerRepository.save(customerEntity);
					});
			});
		}
		CompletableFuture<Void> updateFinancialInfoCF = null;
		if (!CollectionUtils.isEmpty(request.getFinancialInfo())) {
			LOGGER.info("Received a financial info, updating it");
			updateFinancialInfoCF = CompletableFuture.runAsync(() -> {
				Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
					.map(FinancialInfo::valueOf)
					.collect(Collectors.toSet());
				financialClient.updateFinancialInfo(customerId, financialInfo);
			});
		}
		CompletableFuture<Void> updateAddressCF = null;
		if (request.getAddress() != null) {
			LOGGER.info("Received a address, updating it");
			updateAddressCF = CompletableFuture.runAsync(() -> {
				Address address = Address.valueOf(request.getAddress());
				addressClient.updateAddressByCustomerId(customerId, address);
			});
		}
		List<CompletableFuture<Void>> completableFutures = Stream.of(updateCustomerCF, updateFinancialInfoCF, updateAddressCF)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		LOGGER.info("Customer updated successfully!");
		return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
	}

	public CompletableFuture<CustomerResponse> getCustomerById(Integer customerId) {
		LOGGER.info("Getting customer by id {} ", customerId);
		CompletableFuture<Optional<CustomerResponse>> customerResponseCF = CompletableFuture.supplyAsync(
			() -> customerRepository.findById(customerId)
				.map(CustomerResponse::valueOf));
		CompletableFuture<AddressResponse> addressResponseCF = CompletableFuture.supplyAsync(
			() -> addressClient.getAddressByCustomerId(customerId)
				.map(AddressResponse::valueOf)
				.orElse(null));
		CompletableFuture<List<PurchaseTransactionResponse>> purchaseTransactionResponsesCF = CompletableFuture.supplyAsync(
			() -> Stream.ofNullable(purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
				.flatMap(Collection::stream)
				.map(PurchaseTransactionResponse::valueOf)
				.collect(Collectors.toList()));
		CompletableFuture<List<FinancialResponse>> financialResponsesCF = CompletableFuture.supplyAsync(
			() -> Stream.ofNullable(financialClient.getFinancialInfoByCustomerId(customerId))
				.flatMap(Collection::stream)
				.map(FinancialResponse::valueOf)
				.collect(Collectors.toList()));
		CompletableFuture<LoyaltyResponse> loyaltyResponseCF = CompletableFuture.supplyAsync(
			() -> loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
				.map(LoyaltyClientResponse::getPoints)
				.map(LoyaltyResponse::new)
				.orElse(null));

		CompletableFuture<CustomerResponse> response = customerResponseCF
			.thenCombine(addressResponseCF, (customerResponse, addressResponse) -> {
				customerResponse.ifPresent(cr -> cr.setAddressResponse(addressResponse));
				return customerResponse;
			})
			.thenCombine(purchaseTransactionResponsesCF, (customerResponse, purchaseTransactionResponses) -> {
				customerResponse.ifPresent(cr -> cr.setPurchaseTransactions(purchaseTransactionResponses));
				return customerResponse;
			})
			.thenCombine(financialResponsesCF, (customerResponse, financialResponses) -> {
				customerResponse.ifPresent(cr -> cr.setFinancialResponses(financialResponses));
				return customerResponse;
			})
			.thenCombine(loyaltyResponseCF, (customerResponse, loyaltyResponse) -> {
				customerResponse.ifPresent(cr -> cr.setLoyaltyResponse(loyaltyResponse));
				return customerResponse;
			})
			.thenApply(customerResponse -> customerResponse.orElse(null));
		return response;
	}

	public CompletableFuture<CustomerResponse> getCustomerByIdUsingAllOf(Integer customerId) {
		LOGGER.info("Getting customer by id {} using allOf(...)", customerId);
		CompletableFuture<Optional<CustomerResponse>> customerResponseCF = CompletableFuture.supplyAsync(
			() -> customerRepository.findById(customerId)
				.map(CustomerResponse::valueOf));
		CompletableFuture<AddressResponse> addressResponseCF = CompletableFuture.supplyAsync(
			() -> addressClient.getAddressByCustomerId(customerId)
				.map(AddressResponse::valueOf)
				.orElse(null));
		CompletableFuture<List<PurchaseTransactionResponse>> purchaseTransactionResponsesCF = CompletableFuture.supplyAsync(
			() -> Stream.ofNullable(purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
				.flatMap(Collection::stream)
				.map(PurchaseTransactionResponse::valueOf)
				.collect(Collectors.toList()));
		CompletableFuture<List<FinancialResponse>> financialResponsesCF = CompletableFuture.supplyAsync(
			() -> Stream.ofNullable(financialClient.getFinancialInfoByCustomerId(customerId))
				.flatMap(Collection::stream)
				.map(FinancialResponse::valueOf)
				.collect(Collectors.toList()));
		CompletableFuture<LoyaltyResponse> loyaltyResponseCF = CompletableFuture.supplyAsync(
			() -> loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
				.map(LoyaltyClientResponse::getPoints)
				.map(LoyaltyResponse::new)
				.orElse(null));
		CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(
			customerResponseCF, addressResponseCF,
			purchaseTransactionResponsesCF, financialResponsesCF, loyaltyResponseCF);
		Optional<CustomerResponse> customerResponseOptional = customerResponseCF.join();
		CompletableFuture<CustomerResponse> responseCF = voidCompletableFuture
			.thenApply(unusedVariable -> {
					customerResponseOptional.ifPresent(cr -> {
						AddressResponse addressResponse = addressResponseCF.join();
						cr.setAddressResponse(addressResponse);

						List<PurchaseTransactionResponse> purchaseTransactionResponses = purchaseTransactionResponsesCF.join();
						cr.setPurchaseTransactions(purchaseTransactionResponses);

						List<FinancialResponse> financialResponses = financialResponsesCF.join();
						cr.setFinancialResponses(financialResponses);

						LoyaltyResponse loyaltyResponse = loyaltyResponseCF.join();
						cr.setLoyaltyResponse(loyaltyResponse);
					});
					return customerResponseOptional
						.orElse(null);
				}
			);
		return responseCF;
	}
}