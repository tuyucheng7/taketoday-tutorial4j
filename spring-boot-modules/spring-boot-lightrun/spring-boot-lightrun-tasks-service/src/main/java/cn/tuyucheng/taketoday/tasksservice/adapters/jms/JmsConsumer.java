package cn.tuyucheng.taketoday.tasksservice.adapters.jms;

import cn.tuyucheng.taketoday.tasksservice.service.DeletedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class JmsConsumer {
	@Autowired
	private DeletedUserService deletedUserService;

	@JmsListener(destination = "deleted_user")
	public void receive(String user) {
		deletedUserService.handleDeletedUser(user);
	}
}