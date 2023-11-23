package cn.tuyucheng.taketoday.multipledb.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.tuyucheng.taketoday.multipledb.model.user.Possession;

public interface PossessionRepository extends JpaRepository<Possession, Long> {

}