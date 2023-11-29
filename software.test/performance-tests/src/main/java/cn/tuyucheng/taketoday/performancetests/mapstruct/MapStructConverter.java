package cn.tuyucheng.taketoday.performancetests.mapstruct;

import cn.tuyucheng.taketoday.performancetests.Converter;
import cn.tuyucheng.taketoday.performancetests.model.destination.DestinationCode;
import cn.tuyucheng.taketoday.performancetests.model.destination.Order;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceCode;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapStructConverter extends Converter {
   MapStructConverter MAPPER = Mappers.getMapper(MapStructConverter.class);

   @Mapping(source = "status", target = "orderStatus")
   @Override
   Order convert(SourceOrder sourceOrder);

   @Override
   DestinationCode convert(SourceCode sourceCode);
}