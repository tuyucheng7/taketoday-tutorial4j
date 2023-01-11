package cn.tuyucheng.taketoday.roles.rolesauthorities.persistence;

import cn.tuyucheng.taketoday.roles.rolesauthorities.model.User;

public interface UserService {

    User findUserByEmail(String email);
}