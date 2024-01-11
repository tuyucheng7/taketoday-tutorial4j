package cn.tuyucheng.taketoday.spring.insertableonly.entitymanager;

public interface PersistableEntityManagerBookRepository<S> {
   S persist(S entity);
}