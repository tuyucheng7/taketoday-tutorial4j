package cn.tuyucheng.taketoday.springbootredis.service.cache.session;

import cn.tuyucheng.taketoday.springbootredis.model.Session;

import java.util.List;

public interface SessionCache {
	Session createASession(Session session);

	Session getSession(String id);

	List<Session> getAllSessions();
}