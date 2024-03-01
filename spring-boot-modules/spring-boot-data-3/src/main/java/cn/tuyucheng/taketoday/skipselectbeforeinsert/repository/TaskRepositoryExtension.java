package cn.tuyucheng.taketoday.skipselectbeforeinsert.repository;

import cn.tuyucheng.taketoday.skipselectbeforeinsert.model.Task;

public interface TaskRepositoryExtension {
   Task persistAndFlush(Task task);
}