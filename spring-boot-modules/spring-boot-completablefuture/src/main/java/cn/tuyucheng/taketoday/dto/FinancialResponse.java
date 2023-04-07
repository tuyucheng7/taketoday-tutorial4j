package cn.tuyucheng.taketoday.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialResponse {

	private String id;

	private String creditCardNumber;

	private String iban;

	public static FinancialResponse valueOf(FinancialInfo financialInfo) {
		return builder()
			.id(financialInfo.getId())
			.creditCardNumber(financialInfo.getCreditCardNumber())
			.iban(financialInfo.getIban())
			.build();
	}
}