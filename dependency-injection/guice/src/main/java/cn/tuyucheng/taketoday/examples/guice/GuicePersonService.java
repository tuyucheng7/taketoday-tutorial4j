package cn.tuyucheng.taketoday.examples.guice;

import cn.tuyucheng.taketoday.examples.common.PersonDao;
import com.google.inject.Inject;

public class GuicePersonService {

	@Inject
	private PersonDao personDao;

	public PersonDao getPersonDao() {
		return personDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

}
