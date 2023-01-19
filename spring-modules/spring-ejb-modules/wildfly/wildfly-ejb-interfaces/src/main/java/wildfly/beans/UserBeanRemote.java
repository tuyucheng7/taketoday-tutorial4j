package wildfly.beans;

import model.User;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface UserBeanRemote {

	List<User> getUsers();
}
