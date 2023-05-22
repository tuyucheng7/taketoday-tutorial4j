package cn.tuyucheng.taketoday.dozer;

import cn.tuyucheng.taketoday.dozer.dto.UserDTO;
import cn.tuyucheng.taketoday.dozer.entity.UserEntity;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DozerApp.class)
class DozerIntegrationTest {

   @Autowired
   private Mapper mapper;

   @Test
   void givenDTO_whenMapperToEntityUsingDozer_thenCorrect() {
      // Given
      UserDTO userDTO = new UserDTO();
      userDTO.setUserId("100");
      userDTO.setUserName("Doe");
      userDTO.setUserAge(2);
      userDTO.setAddress("Sweden");
      userDTO.setBirthday("2020-07-04");

      // When
      UserEntity entity = mapper.map(userDTO, UserEntity.class);

      // Then
      assertThat(entity).isNotNull().satisfies(e -> {
         assertThat(e.getId()).isEqualTo("100");
         assertThat(e.getName()).isEqualTo("Doe");
         assertThat(e.getAge()).isEqualTo(2);
         assertThat(e.getAddress()).isEqualTo("Sweden");
         assertThat(e.getBirthday()).isEqualTo("2020-07-04");
      });
   }

   @Test
   void givenDTOAndEntityOfInitialId_whenMapperToEntityUsingDozer_thenEntityIdShouldBeOverridden() {
      // Given
      UserDTO userDTO = new UserDTO();
      userDTO.setUserId("100");
      userDTO.setUserName("Doe");
      userDTO.setUserAge(5);
      userDTO.setAddress("Sweden");
      userDTO.setBirthday("2017-07-04");

      UserEntity entity = new UserEntity();
      entity.setId("200");

      assertThat(entity.getId()).isEqualTo("200");

      // When
      mapper.map(userDTO, entity);

      // Then
      assertThat(entity).isNotNull().satisfies(e -> {
         assertThat(e.getId()).isEqualTo("100"); // overridden by userDTO
         assertThat(e.getName()).isEqualTo("Doe");
         assertThat(e.getAge()).isEqualTo(5);
         assertThat(e.getAddress()).isEqualTo("Sweden");
         assertThat(e.getBirthday()).isEqualTo("2017-07-04");
      });
   }

   @Test
   void givenDTO_whenMapperToEntityUsingSpecifiedMapId_thenCorrect() {
      // Given
      UserDTO userDTO = new UserDTO();
      userDTO.setUserId("100");
      userDTO.setUserName("Doe");
      userDTO.setUserAge(3);
      userDTO.setAddress("Sweden");

      UserEntity entity = new UserEntity();

      assertThat(entity.getName()).isNull();
      assertThat(entity.getAge()).isEqualTo(0);

      // When
      mapper.map(userDTO, entity, "user"); // specify the map-id as user

      // Then
      assertThat(entity).isNotNull().satisfies(e -> {
         assertThat(e.getId()).isEqualTo("100");
         assertThat(e.getName()).isEqualTo("Doe");
         assertThat(e.getAge()).isEqualTo(3);
         assertThat(e.getAddress()).isEqualTo("Sweden");
      });
   }
}