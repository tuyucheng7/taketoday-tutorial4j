package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.User;
import cn.tuyucheng.taketoday.web.util.SearchCriteria;

import java.util.List;

public interface IUserDAO {
    List<User> searchUser(List<SearchCriteria> params);

    void save(User entity);
}