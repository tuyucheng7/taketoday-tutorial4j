package cn.tuyucheng.taketoday.deserializationfilters.service;

import cn.tuyucheng.taketoday.deserializationfilters.pojo.ContextSpecific;

import java.io.InputStream;
import java.util.Set;

public interface DeserializationService {

   Set<ContextSpecific> process(InputStream... inputStreams);
}
