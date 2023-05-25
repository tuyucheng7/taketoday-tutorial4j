package cn.tuyucheng.taketoday.mapstruct.mapper.custom

import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.util.*
import kotlin.test.assertEquals


class BeforeAndAfterMappingUserMapperUnitTest {
   private var userMapper: cn.tuyucheng.taketoday.mapstruct.mapper.UserMapper = Mappers.getMapper(cn.tuyucheng.taketoday.mapstruct.mapper.custom.BeforeAndAfterMappingUserMapper::class.java)

   @Test
   fun `should map entity to dto`() {
      val user = cn.tuyucheng.taketoday.mapstruct.entity.User(
            id = 1,
            name = "John Doe",
            createdAt = Date().apply {
               time = 1679682600000 // 2023-03-24 18:30:00
            },
            address = cn.tuyucheng.taketoday.mapstruct.entity.Address(
                  streetAddress = "123 Main St",
                  zipCode = "12345"
            ),
      )
      val userDto = userMapper.toDto(user)
      assertEquals("Mr. John Doe", userDto.name)
   }

   @Test
   fun `should map dto to entity`() {
      val userDto = cn.tuyucheng.taketoday.mapstruct.dto.UserDto(
            id = 1,
            name = "Mr. John Doe",
            createdOn = "2023-03-24 18:30:00",
            address = cn.tuyucheng.taketoday.mapstruct.dto.AddressDto(
                  streetAddress = "123 Main St",
                  zipCode = "12345"
            ),
            status = "ACTIVE"
      )
      val user = userMapper.toBean(userDto)
      assertEquals("John Doe", user.name)
   }
}