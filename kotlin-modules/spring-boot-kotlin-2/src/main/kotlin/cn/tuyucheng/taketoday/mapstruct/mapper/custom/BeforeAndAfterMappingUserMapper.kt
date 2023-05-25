package cn.tuyucheng.taketoday.mapstruct.mapper.custom

import org.mapstruct.AfterMapping
import org.mapstruct.BeforeMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget

@Mapper
abstract class BeforeAndAfterMappingUserMapper : cn.tuyucheng.taketoday.mapstruct.mapper.UserMapper {
   @BeforeMapping
   fun beforeMapping(userDto: cn.tuyucheng.taketoday.mapstruct.dto.UserDto) {
      userDto.name = userDto.name.removePrefix("Mr. ");
   }

   @AfterMapping
   fun afterMapping(@MappingTarget userDto: cn.tuyucheng.taketoday.mapstruct.dto.UserDto) {
      userDto.name = "Mr. " + userDto.name;
   }
}