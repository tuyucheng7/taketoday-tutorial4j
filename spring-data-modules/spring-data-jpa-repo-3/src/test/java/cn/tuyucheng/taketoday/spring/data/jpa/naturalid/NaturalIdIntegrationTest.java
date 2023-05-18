package cn.tuyucheng.taketoday.spring.data.jpa.naturalid;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.entity.ConferenceRoom;
import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.entity.GuestRoom;
import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.repository.ConferenceRoomRepository;
import cn.tuyucheng.taketoday.spring.data.jpa.naturalid.repository.GuestRoomJpaRepository;

@SpringBootTest
class NaturalIdIntegrationTest {

   @Autowired
   private HotelRoomsService service;

   @Autowired
   private GuestRoomJpaRepository guestRoomJpaRepository;
   @Autowired
   private ConferenceRoomRepository conferenceRoomRepository;

   @Test
   void whenWeFindByNaturalKey_thenEntityIsReturnedCorrectly() {
      guestRoomJpaRepository.save(new GuestRoom(23, 3, "B-423", 4));

      Optional<GuestRoom> result = service.guestRoom(23, 3);

      assertThat(result).isPresent()
            .hasValueSatisfying(room -> "B-423".equals(room.getName()));
   }

   @Test
   void whenWeFindBySimpleNaturalKey_thenEntityIsReturnedCorrectly() {
      conferenceRoomRepository.save(new ConferenceRoom("Colorado", 100));

      Optional<ConferenceRoom> result = service.conferenceRoom("Colorado");

      assertThat(result).isPresent()
            .hasValueSatisfying(room -> "Colorado".equals(room.getName()));
   }

   @Test
   void givenNaturalIdRepository_whenWeFindBySimpleNaturalKey_thenEntityIsReturnedCorrectly() {
      conferenceRoomRepository.save(new ConferenceRoom("Nevada", 200));

      Optional<ConferenceRoom> result = conferenceRoomRepository.naturalId("Nevada");

      assertThat(result).isPresent()
            .hasValueSatisfying(room -> "Nevada".equals(room.getName()));
   }
}