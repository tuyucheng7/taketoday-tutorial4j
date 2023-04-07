package cn.tuyucheng.taketoday.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

	private String id;

	private String street;

	private String streetNumber;

	private String city;

	private String state;

	private String zipCode;

	private String country;

	public static AddressResponse valueOf(Address address) {
		return AddressResponse.builder()
			.id(address.getId())
			.street(address.getStreet())
			.streetNumber(address.getStreetNumber())
			.city(address.getCity())
			.state(address.getState())
			.zipCode(address.getZipCode())
			.country(address.getCountry())
			.build();
	}
}