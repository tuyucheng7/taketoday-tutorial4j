package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.SimpleSource;
import cn.tuyucheng.taketoday.entity.SimpleDestination;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:applicationContext.xml")
class SimpleDestinationMapperUsingInjectedIntegrationTest {

   @Autowired
   private SimpleDestinationMapperUsingInjectedService mapper;

   @Test
   void givenSourceToDestination_whenMaps_thenNameEnriched() {
      // Given
      SimpleSource source = new SimpleSource();
      source.setName("Bob");
      source.setDescription("The Builder");

      // When
      SimpleDestination destination = mapper.sourceToDestination(source);

      // Then
      assertThat(destination).isNotNull();
      assertThat(destination.getName()).isEqualTo("-:: Bob ::-");
      assertThat(destination.getDescription()).isEqualTo("The Builder");
   }
}