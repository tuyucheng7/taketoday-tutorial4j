package cn.tuyucheng.taketoday.configuration;

import cn.tuyucheng.taketoday.dto.Address;
import cn.tuyucheng.taketoday.dto.FinancialInfo;
import cn.tuyucheng.taketoday.dto.PurchaseTransaction;
import cn.tuyucheng.taketoday.entity.CustomerEntity;
import cn.tuyucheng.taketoday.repository.CustomerRepository;
import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Data
@Component
@RequiredArgsConstructor
public class DataLoader {

	private final CustomerRepository customerRepository;

	private Map<Integer, Set<PurchaseTransaction>> purchaseTransactionResponses = new HashMap<>();
	private Map<Integer, Address> addressClientResponses = new HashMap<>();
	private Map<Integer, Set<FinancialInfo>> financialResponses = new HashMap<>();
	private Map<Integer, Long> points = new HashMap<>();

	@Bean
	private InitializingBean sendDatabase() {
		Faker faker = new Faker();
		return () -> customerRepository.saveAll(generateCustomerList(faker));
	}

	List<CustomerEntity> generateCustomerList(Faker faker) {
		return IntStream.range(1, 10)
			.mapToObj(i -> {
				CustomerEntity customer = createCustomer(i, faker);
				Set<PurchaseTransaction> purchaseTransactionList = generatePurchaseTransactionList(faker);
				purchaseTransactionResponses.put(customer.getId(), purchaseTransactionList);
				addressClientResponses.put(customer.getId(), createAddress(faker));
				financialResponses.put(customer.getId(), generateFinancialInfo(faker));
				generatePoints(customer, purchaseTransactionList);
				return customer;
			})
			.collect(toList());
	}

	private Address createAddress(Faker faker) {
		return Address.builder()
			.id(UUID.randomUUID().toString())
			.street(faker.address().streetName())
			.streetNumber(faker.address().streetAddressNumber())
			.city(faker.address().city())
			.state(faker.address().state())
			.zipCode(faker.address().zipCode())
			.country(faker.address().country())
			.build();
	}

	private CustomerEntity createCustomer(int customerId, Faker faker) {
		return CustomerEntity.builder()
			.id(customerId)
			.createdAt(
				LocalDate.now().minus(Period.ofDays((new Random().nextInt(365 * 10)))))
			.fullName(faker.name().fullName())
			.phoneNumber(faker.phoneNumber().cellPhone())
			.build();
	}

	private Set<PurchaseTransaction> generatePurchaseTransactionList(Faker faker) {
		return IntStream.range(0, new Random().nextInt(10 - 1 + 1) + 1)
			.mapToObj(i -> PurchaseTransaction.builder()
				.id(UUID.randomUUID().toString())
				.createdAt(
					LocalDate.now().minus(Period.ofDays((new Random().nextInt(365 * 10)))))
				.amount(new BigDecimal(faker.commerce().price().replaceAll(",", ".")))
				.paymentType(List.of(CreditCardType.values())
					.get(new Random().nextInt(CreditCardType.values().length)).toString())
				.build())
			.collect(Collectors.toSet());
	}

	private Set<FinancialInfo> generateFinancialInfo(Faker faker) {
		int endExclusive = new Random().nextInt(4 - 1 + 1) + 1;
		return IntStream.range(0, endExclusive)
			.mapToObj(i -> FinancialInfo.builder()
				.id(UUID.randomUUID().toString())
				.creditCardNumber(faker.finance().creditCard())
				.iban(faker.finance().iban())
				.build())
			.collect(Collectors.toSet());
	}

	private void generatePoints(
		CustomerEntity customer,
		Set<PurchaseTransaction> purchaseTransactionList
	) {
		Long rate = Math.abs(ChronoUnit.YEARS.between(LocalDate.now(), customer.getCreatedAt()));
		Long points = purchaseTransactionList.stream()
			.map(PurchaseTransaction::getAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.multiply(BigDecimal.valueOf(rate == 0 ? 1 : rate)).longValue();
		this.points.put(customer.getId(), points);
	}
}