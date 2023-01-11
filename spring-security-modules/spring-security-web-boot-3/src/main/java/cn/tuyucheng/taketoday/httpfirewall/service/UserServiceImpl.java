package cn.tuyucheng.taketoday.httpfirewall.service;

import cn.tuyucheng.taketoday.httpfirewall.dao.InMemoryUserDao;
import cn.tuyucheng.taketoday.httpfirewall.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl {
    private final InMemoryUserDao inMemoryUserDao;

    public UserServiceImpl(InMemoryUserDao inMemoryUserDao) {
        this.inMemoryUserDao = inMemoryUserDao;
    }

    public void saveUser(User user) {
        inMemoryUserDao.save(user);
    }

    public Optional<User> findById(String userId) {
        return inMemoryUserDao.findById(userId);
    }

    public Optional<List<User>> findAll() {
        return inMemoryUserDao.findAll();
    }

    public void deleteUser(String userId) {
        inMemoryUserDao.delete(userId);
    }
}