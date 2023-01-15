package cn.tuyucheng.taketoday.easymock;

import java.util.List;

public interface IUserService {
	public boolean addUser(User user);

	public List<User> findByEmail(String email);

	public List<User> findByAge(double age);
}
