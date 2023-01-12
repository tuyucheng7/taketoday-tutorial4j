package cn.tuyucheng.taketoday.unmappedproperties.mapper;

import cn.tuyucheng.taketoday.unmappedproperties.dto.CarDTO;
import cn.tuyucheng.taketoday.unmappedproperties.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {
	CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

	CarDTO carToCarDTO(Car car);
}