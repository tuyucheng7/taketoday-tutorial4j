package cn.tuyucheng.taketoday.dtopattern.api;

import cn.tuyucheng.taketoday.dtopattern.domain.Role;
import cn.tuyucheng.taketoday.dtopattern.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class Mapper {
	public UserDTO toDto(User user) {
		String name = user.getName();
		List<String> roles = user
			.getRoles()
			.stream()
			.map(Role::getName)
			.toList();

		return new UserDTO(name, roles);
	}

	public User toUser(UserCreationDTO userDTO) {
		return new User(userDTO.getName(), userDTO.getPassword(), new ArrayList<>());
	}
}