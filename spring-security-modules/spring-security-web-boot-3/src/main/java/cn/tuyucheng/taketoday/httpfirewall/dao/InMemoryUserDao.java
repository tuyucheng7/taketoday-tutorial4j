package cn.tuyucheng.taketoday.httpfirewall.dao;

import cn.tuyucheng.taketoday.httpfirewall.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserDao {
    private final Map<String, User> map = new HashMap<>();

    public void save(User user) {
        map.put(user.getId(), user);
    }

    public Optional<User> findById(String userId) {
        return Optional.ofNullable(map.get(userId));
    }

    public Optional<List<User>> findAll() {
        return Optional.of(new ArrayList<>(map.values()));
    }

    public void delete(String userId) {
        map.remove(userId);
    }

    public boolean isExists(String userId) {
        return map.containsKey(userId);
    }
}