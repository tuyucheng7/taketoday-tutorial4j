package cn.tuyucheng.taketoday.performancetests.modelmapper;

import cn.tuyucheng.taketoday.performancetests.Converter;
import cn.tuyucheng.taketoday.performancetests.model.destination.DestinationCode;
import cn.tuyucheng.taketoday.performancetests.model.destination.Order;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceCode;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceOrder;
import org.modelmapper.ModelMapper;

public class ModelMapperConverter implements Converter {
   private ModelMapper modelMapper;

   public ModelMapperConverter() {
      modelMapper = new ModelMapper();
   }

   @Override
   public Order convert(SourceOrder sourceOrder) {
      return modelMapper.map(sourceOrder, Order.class);
   }

   @Override
   public DestinationCode convert(SourceCode sourceCode) {
      return modelMapper.map(sourceCode, DestinationCode.class);
   }
}