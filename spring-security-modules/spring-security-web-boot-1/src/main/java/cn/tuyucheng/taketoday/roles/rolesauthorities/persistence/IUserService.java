package cn.tuyucheng.taketoday.roles.rolesauthorities.persistence;

import cn.tuyucheng.taketoday.roles.rolesauthorities.model.User;

public interface IUserService {

    User findUserByEmail(String email);

}
