package cn.tuyucheng.taketoday.spring.data.jpa.naturalid.repository;

import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.entity.ConferenceRoom;

import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRoomRepository extends NaturalIdRepository<ConferenceRoom, String> {
}