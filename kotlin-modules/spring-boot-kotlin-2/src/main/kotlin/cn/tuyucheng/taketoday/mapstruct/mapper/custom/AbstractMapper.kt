package cn.tuyucheng.taketoday.mapstruct.mapper.custom

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.text.SimpleDateFormat

@Mapper
abstract class AbstractMapper {

   fun toDto(user: cn.tuyucheng.taketoday.mapstruct.entity.User): cn.tuyucheng.taketoday.mapstruct.dto.UserDto {
      return cn.tuyucheng.taketoday.mapstruct.dto.UserDto(
            id = user.id,
            name = "Mr. ${user.name}",
            createdOn = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(user.createdAt),
            address = cn.tuyucheng.taketoday.mapstruct.dto.AddressDto(
                  streetAddress = user.address.streetAddress,
                  zipCode = user.address.zipCode
            ),
            status = "ACTIVE"
      )
   }

   @Mapping(source = "createdOn", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss", defaultExpression = "java(new java.util.Date())")
   @Mapping(source = "status", target = "status", defaultValue = "ACTIVE")
   abstract fun toBean(userDto: cn.tuyucheng.taketoday.mapstruct.dto.UserDto): cn.tuyucheng.taketoday.mapstruct.entity.User

   abstract fun toAddressDto(address: cn.tuyucheng.taketoday.mapstruct.entity.Address): cn.tuyucheng.taketoday.mapstruct.dto.AddressDto
   abstract fun toAddress(addressDto: cn.tuyucheng.taketoday.mapstruct.dto.AddressDto): cn.tuyucheng.taketoday.mapstruct.entity.Address
}