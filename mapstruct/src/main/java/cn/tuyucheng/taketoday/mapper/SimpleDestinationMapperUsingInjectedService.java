package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.SimpleSource;
import cn.tuyucheng.taketoday.entity.SimpleDestination;
import cn.tuyucheng.taketoday.service.SimpleService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SimpleDestinationMapperUsingInjectedService {

	@Autowired
	protected SimpleService simpleService;

	@Mapping(target = "name", expression = "java(simpleService.enrichName(source.getName()))")
	public abstract SimpleDestination sourceToDestination(SimpleSource source);

	public abstract SimpleSource destinationToSource(SimpleDestination destination);


}
