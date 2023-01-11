package cn.tuyucheng.taketoday.osiv.service;

import cn.tuyucheng.taketoday.osiv.model.BasicUser;

import java.util.Optional;

public interface UserService {
    Optional<BasicUser> findOne(String username);
}