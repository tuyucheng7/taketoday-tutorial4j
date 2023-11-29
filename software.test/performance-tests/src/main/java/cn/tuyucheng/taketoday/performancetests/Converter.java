package cn.tuyucheng.taketoday.performancetests;

import cn.tuyucheng.taketoday.performancetests.model.destination.DestinationCode;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceCode;
import cn.tuyucheng.taketoday.performancetests.model.source.SourceOrder;
import cn.tuyucheng.taketoday.performancetests.model.destination.Order;

public interface Converter {
   Order convert(SourceOrder sourceOrder);

   DestinationCode convert(SourceCode sourceCode);
}