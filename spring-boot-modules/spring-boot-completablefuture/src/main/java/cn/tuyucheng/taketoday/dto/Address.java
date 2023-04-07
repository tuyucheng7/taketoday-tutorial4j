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
public class Address {

	private String id;

	private String street;

	private String streetNumber;

	private String city;

	private String state;

	private String zipCode;

	private String country;

	public static Address valueOf(UpdateAddressRequest request) {
		return builder()
			.id(UUID.randomUUID().toString())
			.street(request.getStreet())
			.streetNumber(request.getStreetNumber())
			.city(request.getCity())
			.state(request.getState())
			.zipCode(request.getZipCode())
			.country(request.getCountry())
			.build();
	}
}