package cn.tuyucheng.taketoday.mapper;

import cn.tuyucheng.taketoday.dto.UserBodyImperialValuesDTO;
import cn.tuyucheng.taketoday.entity.UserBodyValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class UserBodyValuesMapperUnitTest {

   @Test
   void givenUserBodyImperialValuesDTOToUserBodyValuesObject_whenMaps_thenCorrect() {
      UserBodyImperialValuesDTO dto = new UserBodyImperialValuesDTO();
      dto.setInch(10);
      dto.setPound(100);

      UserBodyValues obj = UserBodyValuesMapper.INSTANCE.userBodyValuesMapper(dto);

      assertNotNull(obj);
      assertEquals(25.4, obj.getCentimeter(), 0);
      assertEquals(45.35, obj.getKilogram(), 0);
   }

   @Test
   void givenUserBodyImperialValuesDTOWithInchToUserBodyValuesObject_whenMaps_thenCorrect() {
      UserBodyImperialValuesDTO dto = new UserBodyImperialValuesDTO();
      dto.setInch(10);

      UserBodyValues obj = UserBodyValuesMapper.INSTANCE.userBodyValuesMapper(dto);

      assertNotNull(obj);
      assertEquals(25.4, obj.getCentimeter(), 0);
   }

   @Test
   void givenUserBodyImperialValuesDTOWithPoundToUserBodyValuesObject_whenMaps_thenCorrect() {
      UserBodyImperialValuesDTO dto = new UserBodyImperialValuesDTO();
      dto.setPound(100);

      UserBodyValues obj = UserBodyValuesMapper.INSTANCE.userBodyValuesMapper(dto);

      assertNotNull(obj);
      assertEquals(45.35, obj.getKilogram(), 0);
   }
}