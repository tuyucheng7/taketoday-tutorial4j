package cn.tuyucheng.taketoday.inheritancecomposition;

import cn.tuyucheng.taketoday.inheritancecomposition.model.Actress;
import cn.tuyucheng.taketoday.inheritancecomposition.model.Person;
import cn.tuyucheng.taketoday.inheritancecomposition.model.Waitress;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritanceUnitTest {

	@Test
	public void givenWaitressInstance_whenCheckedType_thenIsInstanceOfPerson() {
		assertThat(new Waitress("Mary", "mary@domain.com", 22)).isInstanceOf(Person.class);
	}

	@Test
	public void givenActressInstance_whenCheckedType_thenIsInstanceOfPerson() {
		assertThat(new Actress("Susan", "susan@domain.com", 30)).isInstanceOf(Person.class);
	}
}
