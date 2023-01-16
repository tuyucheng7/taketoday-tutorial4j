package cn.tuyucheng.taketoday.security;

public interface ISecurityUserService {

	String validatePasswordResetToken(String token);

}
