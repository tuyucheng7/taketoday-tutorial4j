package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.SimpleSource;
import cn.tuyucheng.taketoday.entity.SimpleDestination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:applicationContext.xml")
class SimpleSourceDestinationMapperIntegrationTest {

   @Autowired
   SimpleSourceDestinationMapper simpleSourceDestinationMapper;

   @Test
   void givenSourceToDestination_whenMaps_thenCorrect() {
      SimpleSource simpleSource = new SimpleSource();
      simpleSource.setName("SourceName");
      simpleSource.setDescription("SourceDescription");

      SimpleDestination destination = simpleSourceDestinationMapper.sourceToDestination(simpleSource);

      assertEquals(simpleSource.getName(), destination.getName());
      assertEquals(simpleSource.getDescription(), destination.getDescription());
   }

   @Test
   void givenDestinationToSource_whenMaps_thenCorrect() {
      SimpleDestination destination = new SimpleDestination();
      destination.setName("DestinationName");
      destination.setDescription("DestinationDescription");

      SimpleSource source = simpleSourceDestinationMapper.destinationToSource(destination);

      assertEquals(destination.getName(), source.getName());
      assertEquals(destination.getDescription(), source.getDescription());
   }
}