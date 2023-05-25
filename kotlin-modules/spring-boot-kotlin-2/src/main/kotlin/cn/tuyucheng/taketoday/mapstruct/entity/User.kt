package cn.tuyucheng.taketoday.mapstruct.entity

import java.util.*

class User(
      var name: String,
      val createdAt: Date,
      val id: Long,
      val address: cn.tuyucheng.taketoday.mapstruct.entity.Address,
      val status: String? = null
)