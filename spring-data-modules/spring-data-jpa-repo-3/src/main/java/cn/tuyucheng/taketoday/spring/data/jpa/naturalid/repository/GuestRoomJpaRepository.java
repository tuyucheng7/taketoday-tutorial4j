package cn.tuyucheng.taketoday.spring.data.jpa.naturalid.repository;

import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.entity.GuestRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRoomJpaRepository extends JpaRepository<GuestRoom, Long> {
}