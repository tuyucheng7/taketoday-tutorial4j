package cn.tuyucheng.taketoday.relationships.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import cn.tuyucheng.taketoday.relationships.models.AppUser;

public interface UserRepository extends CrudRepository<AppUser, Long> {

    AppUser findByUsername(String username);

    List<AppUser> findByName(String name);

    @Query("UPDATE AppUser u SET u.lastLogin=:lastLogin WHERE u.username = ?#{ principal?.username }")
    @Modifying
    @Transactional
    void updateLastLogin(@Param("lastLogin") Date lastLogin);
}
