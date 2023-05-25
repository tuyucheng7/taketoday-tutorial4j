package cn.tuyucheng.taketoday.mapstruct.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface UserMapper {
   @Mapping(source = "createdAt", target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
   @Mapping(target = "status", constant = "ACTIVE")
   fun toDto(user: cn.tuyucheng.taketoday.mapstruct.entity.User): cn.tuyucheng.taketoday.mapstruct.dto.UserDto

   @Mapping(source = "createdOn", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss", defaultExpression = "java(new java.util.Date())")
   @Mapping(source = "status", target = "status", defaultValue = "ACTIVE")
   fun toBean(userDto: cn.tuyucheng.taketoday.mapstruct.dto.UserDto): cn.tuyucheng.taketoday.mapstruct.entity.User

   fun toAddressDto(address: cn.tuyucheng.taketoday.mapstruct.entity.Address): cn.tuyucheng.taketoday.mapstruct.dto.AddressDto
   fun toAddress(addressDto: cn.tuyucheng.taketoday.mapstruct.dto.AddressDto): cn.tuyucheng.taketoday.mapstruct.entity.Address
}