package cn.tuyucheng.taketoday.mapstruct.dto

data class UserDto(
      val id: Long,
      var name: String,
      val createdOn: String? = null,
      val address: cn.tuyucheng.taketoday.mapstruct.dto.AddressDto,
      val status: String? = null
)