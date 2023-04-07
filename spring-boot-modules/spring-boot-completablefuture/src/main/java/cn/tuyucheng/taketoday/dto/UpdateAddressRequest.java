package cn.tuyucheng.taketoday.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddressRequest {

	private String street;

	private String streetNumber;

	private String city;

	private String state;

	private String zipCode;

	private String country;
}