package cn.tuyucheng.taketoday.usersservice.service;

import cn.tuyucheng.taketoday.usersservice.adapters.jms.JmsSender;
import cn.tuyucheng.taketoday.usersservice.adapters.repository.UserRecord;
import cn.tuyucheng.taketoday.usersservice.adapters.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private JmsSender jmsSender;

	public UserRecord getUserById(String id) {
		return usersRepository.findById(id)
			.orElseThrow(() -> new UnknownUserException(id));
	}

	@Transactional
	public void deleteUserById(String id) {
		var user = usersRepository.findById(id)
			.orElseThrow(() -> new UnknownUserException(id));
		usersRepository.delete(user);

		jmsSender.sendDeleteUserMessage(id);
	}

	@Transactional
	public UserRecord updateUser(String id, Optional<String> newName) {
		var user = usersRepository.findById(id)
			.orElseThrow(() -> new UnknownUserException(id));

		newName.ifPresent(user::setName);

		return user;
	}

	public UserRecord createUser(String name) {
		var user = new UserRecord(UUID.randomUUID()
			.toString(), name);
		usersRepository.save(user);
		return user;
	}
}