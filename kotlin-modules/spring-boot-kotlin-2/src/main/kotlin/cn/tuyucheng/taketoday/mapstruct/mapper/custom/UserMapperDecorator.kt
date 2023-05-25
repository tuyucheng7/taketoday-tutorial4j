package cn.tuyucheng.taketoday.mapstruct.mapper.custom

import org.mapstruct.factory.Mappers

open class UserMapperDecorator : DecoratedMapper() {

   var userMapper: cn.tuyucheng.taketoday.mapstruct.mapper.UserMapper = Mappers.getMapper(cn.tuyucheng.taketoday.mapstruct.mapper.UserMapper::class.java)

   override fun toDto(user: cn.tuyucheng.taketoday.mapstruct.entity.User): cn.tuyucheng.taketoday.mapstruct.dto.UserDto {
      val userDto = userMapper.toDto(user)
      userDto.name = "Mr. " + userDto.name
      return userDto
   }

   override fun toBean(userDto: cn.tuyucheng.taketoday.mapstruct.dto.UserDto): cn.tuyucheng.taketoday.mapstruct.entity.User {
      return userMapper.toBean(userDto)
   }

   override fun toAddressDto(address: cn.tuyucheng.taketoday.mapstruct.entity.Address): cn.tuyucheng.taketoday.mapstruct.dto.AddressDto {
      return userMapper.toAddressDto(address)
   }

   override fun toAddress(addressDto: cn.tuyucheng.taketoday.mapstruct.dto.AddressDto): cn.tuyucheng.taketoday.mapstruct.entity.Address {
      return userMapper.toAddress(addressDto)
   }
}