package cn.tuyucheng.taketoday;

import cn.tuyucheng.taketoday.persistence.dao.RoleRepository;
import cn.tuyucheng.taketoday.persistence.dao.UserRepository;
import cn.tuyucheng.taketoday.persistence.model.Role;
import cn.tuyucheng.taketoday.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class Setup {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@PostConstruct
	private void setupData() {
		Role roleUser = new Role("ROLE_USER");
		roleUser = roleRepository.save(roleUser);
		Role roleAdmin = new Role("ROLE_ADMIN");
		roleAdmin = roleRepository.save(roleAdmin);

		final User userJohn = new User("john", "john@test.com");
		userJohn.setRoles(new HashSet<Role>(Arrays.asList(roleUser, roleAdmin)));
		userRepository.save(userJohn);

		final User userTom = new User("tom", "tom@test.com");
		userTom.setRoles(new HashSet<Role>(Arrays.asList(roleUser)));
		userRepository.save(userTom);
	}

}