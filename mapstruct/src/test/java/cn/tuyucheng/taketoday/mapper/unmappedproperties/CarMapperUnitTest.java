package cn.tuyucheng.taketoday.mapper.unmappedproperties;

import cn.tuyucheng.taketoday.unmappedproperties.dto.CarDTO;
import cn.tuyucheng.taketoday.unmappedproperties.entity.Car;
import cn.tuyucheng.taketoday.unmappedproperties.mapper.CarMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CarMapperUnitTest {

	@Test
	public void givenCarEntitytoCar_whenMaps_thenCorrect() {
		Car entity = new Car();
		entity.setId(1);
		entity.setName("Toyota");

		CarDTO carDto = CarMapper.INSTANCE.carToCarDTO(entity);

		assertThat(carDto.getId()).isEqualTo(entity.getId());
		assertThat(carDto.getName()).isEqualTo(entity.getName());
	}
}
