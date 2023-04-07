package cn.tuyucheng.taketoday.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInfo {

	private String id;

	private String creditCardNumber;

	private String iban;

	public static FinancialInfo valueOf(FinancialRequest financialInfo) {
		return builder()
			.id(UUID.randomUUID().toString())
			.creditCardNumber(financialInfo.getCreditCardNumber())
			.iban(financialInfo.getIban())
			.build();
	}
}