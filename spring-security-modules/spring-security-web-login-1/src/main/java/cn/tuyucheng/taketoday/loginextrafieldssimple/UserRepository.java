package cn.tuyucheng.taketoday.loginextrafieldssimple;

public interface UserRepository {

    public User findUser(String username, String domain);

}
