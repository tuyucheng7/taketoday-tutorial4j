package cn.tuyucheng.taketoday.deserializationfilters.service;

import cn.tuyucheng.taketoday.deserializationfilters.pojo.ContextSpecific;
import cn.tuyucheng.taketoday.deserializationfilters.utils.DeserializationUtils;

import java.io.InputStream;
import java.util.Set;

public class SmallObjectService implements DeserializationService {

   @Override
   public Set<ContextSpecific> process(InputStream... inputStreams) {
      return DeserializationUtils.deserializeIntoSet(inputStreams);
   }
}