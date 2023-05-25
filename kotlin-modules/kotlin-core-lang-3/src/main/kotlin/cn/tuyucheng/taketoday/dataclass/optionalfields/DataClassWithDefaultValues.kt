package cn.tuyucheng.taketoday.dataclass.optionalfields

class DataClassWithDefaultValues(
      val name: String = "",
      val surname: String = "",
      val age: Number = Int.MIN_VALUE
)