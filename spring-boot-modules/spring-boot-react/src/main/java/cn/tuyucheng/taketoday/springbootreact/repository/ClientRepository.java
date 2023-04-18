package cn.tuyucheng.taketoday.springbootreact.repository;

import cn.tuyucheng.taketoday.springbootreact.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}