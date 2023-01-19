package cn.tuyucheng.taketoday.graphql.error.handling.service;

import cn.tuyucheng.taketoday.graphql.error.handling.domain.Location;
import cn.tuyucheng.taketoday.graphql.error.handling.domain.Vehicle;
import cn.tuyucheng.taketoday.graphql.error.handling.exception.InvalidInputException;
import cn.tuyucheng.taketoday.graphql.error.handling.exception.VehicleAlreadyPresentException;
import cn.tuyucheng.taketoday.graphql.error.handling.exception.VehicleNotFoundException;
import cn.tuyucheng.taketoday.graphql.error.handling.repository.InventoryRepository;
import cn.tuyucheng.taketoday.graphql.error.handling.repository.LocationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {
	private final InventoryRepository inventoryRepository;
	private final LocationRepository locationRepository;

	public InventoryService(InventoryRepository inventoryRepository, LocationRepository locationRepository) {
		this.inventoryRepository = inventoryRepository;
		this.locationRepository = locationRepository;
	}

	@Transactional
	public Vehicle addVehicle(String vin, Integer year, String make, String model, String trim, Location location) {
		Optional<Vehicle> existingVehicle = this.inventoryRepository.findById(vin);
		if (existingVehicle.isPresent()) {
			Map<String, Object> params = new HashMap<>();
			params.put("vin", vin);
			throw new VehicleAlreadyPresentException("Failed to add vehicle. Vehicle with vin already present.", params);
		}
		Vehicle vehicle = Vehicle.builder()
			.vin(vin)
			.year(year)
			.make(make)
			.model(model)
			.location(location)
			.trim(trim)
			.build();

		this.locationRepository.save(location);
		return this.inventoryRepository.save(vehicle);
	}

	public List<Vehicle> searchAll() {
		return this.inventoryRepository.findAll();
	}

	public List<Vehicle> searchByLocation(String zipcode) {
		if (StringUtils.isEmpty(zipcode) || zipcode.length() != 5) {
			throw new InvalidInputException("Invalid zipcode " + zipcode + " provided.");
		}
		return this.locationRepository.findById(zipcode)
			.map(Location::getVehicles)
			.orElse(new ArrayList<>());
	}

	public Vehicle searchByVin(String vin) {
		return this.inventoryRepository.findById(vin)
			.orElseThrow(() -> new VehicleNotFoundException("Vehicle with vin: " + vin + " not found."));
	}
}