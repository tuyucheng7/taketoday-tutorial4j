package cn.tuyucheng.taketoday.osiv.repository;

import cn.tuyucheng.taketoday.osiv.model.BasicUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {

    @EntityGraph(attributePaths = "permissions")
    Optional<BasicUser> findDetailedByUsername(String username);
}