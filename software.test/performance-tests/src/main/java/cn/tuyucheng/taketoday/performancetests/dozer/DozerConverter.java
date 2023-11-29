package cn.tuyucheng.taketoday.performancetests.dozer;

import cn.tuyucheng.taketoday.performancetests.Converter;
import cn.tuyucheng.taketoday.performancetests.model.destination.DestinationCode;
import cn.tuyucheng.taketoday.performancetests.model.destination.Order;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceCode;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceOrder;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

public class DozerConverter implements Converter {
   private final Mapper mapper;

   public DozerConverter() {
      this.mapper = DozerBeanMapperBuilder.create()
            .withMappingFiles("dozer-mapping.xml")
            .build();
   }

   @Override
   public Order convert(SourceOrder sourceOrder) {
      return mapper.map(sourceOrder, Order.class);
   }

   @Override
   public DestinationCode convert(SourceCode sourceCode) {
      return mapper.map(sourceCode, DestinationCode.class);
   }
}