package cn.tuyucheng.taketoday.loginextrafieldscustom;

public interface UserRepository {

    public User findUser(String username, String domain);

}
