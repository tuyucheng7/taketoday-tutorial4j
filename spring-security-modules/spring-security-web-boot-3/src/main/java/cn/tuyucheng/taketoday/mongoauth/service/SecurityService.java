package cn.tuyucheng.taketoday.mongoauth.service;

public interface SecurityService {
    boolean login(String username, String password);
}
