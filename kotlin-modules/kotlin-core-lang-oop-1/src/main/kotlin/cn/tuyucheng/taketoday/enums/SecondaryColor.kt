package cn.tuyucheng.taketoday.enums

enum class SecondaryColor : ISecondaryColor {
   GREEN {
      override fun paint(): String {
         return "green"
      }
   };
}