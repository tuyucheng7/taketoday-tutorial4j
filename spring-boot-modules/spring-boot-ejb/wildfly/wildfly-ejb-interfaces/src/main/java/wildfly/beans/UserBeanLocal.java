package wildfly.beans;

import model.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserBeanLocal {

	List<User> getUsers();
}
