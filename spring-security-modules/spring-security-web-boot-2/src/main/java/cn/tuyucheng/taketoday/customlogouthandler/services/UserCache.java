package cn.tuyucheng.taketoday.customlogouthandler.services;

import cn.tuyucheng.taketoday.customlogouthandler.user.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserCache {

    @PersistenceContext
    private EntityManager entityManager;

    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>(256);

    public User getByUserName(String userName) {
        return store.computeIfAbsent(userName, k -> entityManager.createQuery("from User where login=:login", User.class)
            .setParameter("login", k)
            .getSingleResult());
    }

    public void evictUser(String userName) {
        store.remove(userName);
    }

    public int size() {
        return store.size();
    }

}
