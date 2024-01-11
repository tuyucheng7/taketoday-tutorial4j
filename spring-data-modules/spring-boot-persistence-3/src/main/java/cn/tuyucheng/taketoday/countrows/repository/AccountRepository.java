package cn.tuyucheng.taketoday.countrows.repository;

import cn.tuyucheng.taketoday.countrows.entity.Account;
import cn.tuyucheng.taketoday.countrows.entity.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

   long countByUsername(String username);

   long countByPermission(Permission permission);

   long countByPermissionAndCreatedOnGreaterThan(Permission permission, Timestamp ts);
}