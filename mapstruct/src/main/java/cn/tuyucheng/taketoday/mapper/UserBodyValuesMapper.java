package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.UserBodyImperialValuesDTO;
import cn.tuyucheng.taketoday.entity.UserBodyValues;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserBodyValuesMapper {

	UserBodyValuesMapper INSTANCE = Mappers.getMapper(UserBodyValuesMapper.class);

	@Mapping(source = "pound", target = "kilogram", qualifiedBy = PoundToKilogramMapper.class)
	@Mapping(source = "inch", target = "centimeter", qualifiedByName = "inchToCentimeter")
	public UserBodyValues userBodyValuesMapper(UserBodyImperialValuesDTO dto);

	@Named("inchToCentimeter")
	public static double inchToCentimeter(int inch) {
		return inch * 2.54;
	}

	@PoundToKilogramMapper
	public static double poundToKilogram(int pound) {
		return pound * 0.4535;
	}
}