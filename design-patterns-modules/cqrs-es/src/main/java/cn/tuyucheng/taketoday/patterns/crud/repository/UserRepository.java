package cn.tuyucheng.taketoday.patterns.crud.repository;

import java.util.HashMap;
import java.util.Map;

import cn.tuyucheng.taketoday.patterns.domain.User;

public class UserRepository {

    private Map<String, User> store = new HashMap<>();

    public void addUser(String id, User user) {
        store.put(id, user);
    }

    public User getUser(String id) {
        return store.get(id);
    }

}
