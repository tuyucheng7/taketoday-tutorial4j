package cn.tuyucheng.taketoday.springbean.naming.service;

import cn.tuyucheng.taketoday.springbean.naming.component.Animal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PetShow {

	@Autowired
	@Qualifier("dog")
	private Animal dog;

	@Autowired
	@Qualifier("cat")
	private Animal cat;

	public Animal getDog() {
		return dog;
	}

	public Animal getCat() {
		return cat;
	}
}
