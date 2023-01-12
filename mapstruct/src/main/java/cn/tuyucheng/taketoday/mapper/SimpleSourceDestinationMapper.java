package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.SimpleSource;
import cn.tuyucheng.taketoday.entity.SimpleDestination;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimpleSourceDestinationMapper {

	SimpleDestination sourceToDestination(SimpleSource source);

	SimpleSource destinationToSource(SimpleDestination destination);

}
