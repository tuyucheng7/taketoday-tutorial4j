package cn.tuyucheng.taketoday.spring.data.persistence.customrepository.repository;

import cn.tuyucheng.taketoday.spring.data.persistence.customrepository.model.User;

public interface CustomUserRepository {

    User customFindMethod(Long id);

}
