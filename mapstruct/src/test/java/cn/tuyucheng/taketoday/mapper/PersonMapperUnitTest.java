package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.PersonDTO;
import cn.tuyucheng.taketoday.entity.Person;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PersonMapperUnitTest {

	@Test
	public void givenPersonEntitytoPersonWithExpression_whenMaps_thenCorrect() {

		Person entity = new Person();
		entity.setName("Micheal");

		PersonDTO personDto = PersonMapper.INSTANCE.personToPersonDTO(entity);

		assertNull(entity.getId());
		assertNotNull(personDto.getId());
		assertEquals(personDto.getName(), entity.getName());
	}
}