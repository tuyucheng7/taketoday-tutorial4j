package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.CarDTO;
import cn.tuyucheng.taketoday.dto.FuelType;
import cn.tuyucheng.taketoday.entity.BioDieselCar;
import cn.tuyucheng.taketoday.entity.Car;
import cn.tuyucheng.taketoday.entity.ElectricCar;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.assertEquals;

class CarsMapperUnitTest {

	private CarsMapper sut = Mappers.getMapper(CarsMapper.class);

	@Test
	void testGivenSubTypeElectric_mapsModifiedFieldsToSuperTypeDto_whenBeforeAndAfterMappingMethodscarCalled() {
		Car car = new ElectricCar();
		car.setId(12);
		car.setName("Tesla_Model_C");

		CarDTO carDto = sut.toCarDto(car);

		assertEquals("TESLA_MODEL_C", carDto.getName());
		assertEquals(FuelType.ELECTRIC, carDto.getFuelType());
	}

	@Test
	void testGivenSubTypeBioDiesel_mapsModifiedFieldsToSuperTypeDto_whenBeforeAndAfterMappingMethodscarCalled() {
		Car car = new BioDieselCar();
		car.setId(11);
		car.setName("Tesla_Model_X");

		CarDTO carDto = sut.toCarDto(car);

		assertEquals("TESLA_MODEL_X", carDto.getName());
		assertEquals(FuelType.BIO_DIESEL, carDto.getFuelType());
	}

}
