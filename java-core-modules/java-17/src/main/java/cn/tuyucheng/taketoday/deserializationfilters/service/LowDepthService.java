package cn.tuyucheng.taketoday.deserializationfilters.service;

import cn.tuyucheng.taketoday.deserializationfilters.pojo.ContextSpecific;
import cn.tuyucheng.taketoday.deserializationfilters.utils.DeserializationUtils;

import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.util.Set;

public class LowDepthService implements DeserializationService {

   public Set<ContextSpecific> process(ObjectInputFilter filter, InputStream... inputStreams) {
      return DeserializationUtils.deserializeIntoSet(filter, inputStreams);
   }

   @Override
   public Set<ContextSpecific> process(InputStream... inputStreams) {
      return process(null, inputStreams);
   }
}
