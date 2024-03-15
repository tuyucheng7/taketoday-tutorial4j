package cn.tuyucheng.taketoday.skipselectbeforeinsert.repository;

import cn.tuyucheng.taketoday.skipselectbeforeinsert.model.Task;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class TaskRepositoryExtensionImpl implements TaskRepositoryExtension {
   @PersistenceContext
   private EntityManager entityManager;

   @Override
   public Task persistAndFlush(Task task) {
      entityManager.persist(task);
      entityManager.flush();
      return task;
   }
}