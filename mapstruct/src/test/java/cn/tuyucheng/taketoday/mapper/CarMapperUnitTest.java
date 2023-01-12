package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.CarDTO;
import cn.tuyucheng.taketoday.entity.Car;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CarMapperUnitTest {

	@Test
	public void givenCarEntitytoCar_whenMaps_thenCorrect() {

		Car entity = new Car();
		entity.setId(1);
		entity.setName("Toyota");

		CarDTO carDto = CarMapper.INSTANCE.carToCarDTO(entity);

		assertEquals(carDto.getId(), entity.getId());
		assertEquals(carDto.getName(), entity.getName());
	}
}
