package cn.tuyucheng.taketoday.batch.understanding;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
public class SimpleChunkWriter extends AbstractItemWriter {
   List<Integer> processed = new ArrayList<>();

   @Override
   public void writeItems(List<Object> items) throws Exception {
      items.stream().map(Integer.class::cast).forEach(this.processed::add);
   }
}
