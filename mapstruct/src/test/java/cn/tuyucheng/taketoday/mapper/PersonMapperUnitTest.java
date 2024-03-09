package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.PersonDTO;
import cn.tuyucheng.taketoday.entity.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonMapperUnitTest {

   @Test
   void givenPersonEntitytoPersonWithExpression_whenMaps_thenCorrect() {
      Person entity = new Person();
      entity.setName("Micheal");

      PersonDTO personDto = PersonMapper.INSTANCE.personToPersonDTO(entity);

      assertNull(entity.getId());
      assertNotNull(personDto.getId());
      assertEquals(personDto.getName(), entity.getName());
   }
}